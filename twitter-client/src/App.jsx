import { useState, useEffect, useCallback, useRef } from 'react';
import './App.css';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3000';

const parseErrorMessage = async (response, fallbackMsg) => {
  try {
    const data = await response.json();
    return data.message || fallbackMsg;
  } catch {
    return fallbackMsg;
  }
};

function App() {
  const [userId, setUserId] = useState('1');
  const initialUserId = useRef(userId);
  const [tweets, setTweets] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [corsStatus, setCorsStatus] = useState('BAŞARILI');
  const [showCorsModal, setShowCorsModal] = useState(false);
  const [activeTab, setActiveTab] = useState('login'); // 'login' | 'register'

  // Auth States
  const [storedUserId, setStoredUserId] = useState(localStorage.getItem('user_id') || '1');
  const [token, setToken] = useState(localStorage.getItem('jwt_token') || '');
  const [username, setUsername] = useState(localStorage.getItem('username') || '');
  const [loginForm, setLoginForm] = useState({ username: '', password: '' });
  const [registerForm, setRegisterForm] = useState({ username: '', password: '' });
  const [tweetContent, setTweetContent] = useState('');
  const [authMessage, setAuthMessage] = useState(null);

  // Yorum ekleme state'leri
  const [activeCommentTweetId, setActiveCommentTweetId] = useState(null);
  const [commentContent, setCommentContent] = useState('');

  const fetchUserTweets = useCallback(async (targetUserId) => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`${API_BASE_URL}/tweet/findByUserId?userId=${targetUserId}`);
      if (!response.ok) {
        const msg = await parseErrorMessage(response, `HTTP Hata: ${response.status}`);
        throw new Error(msg);
      }
      const data = await response.json();
      setTweets(data);
      setCorsStatus('BAŞARILI');
    } catch (err) {
      console.error('Tweet getirme hatası:', err);
      setError(err.message);
      setCorsStatus('Bağlantı Hatası / Server Down');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchUserTweets(initialUserId.current);
  }, [fetchUserTweets]);

  const handleFetchClick = (e) => {
    e.preventDefault();
    if (userId.trim()) {
      fetchUserTweets(userId.trim());
    }
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setAuthMessage(null);
    try {
      const res = await fetch(`${API_BASE_URL}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(loginForm),
      });

      if (res.ok) {
        // Mock token logic (Backend response tipine göre uyarlandı)
        const data = await res.json().catch(() => ({}));
        const currentId = String(data.id ?? storedUserId ?? '1');
        setStoredUserId(currentId);
        setUserId(currentId);
        fetchUserTweets(currentId);
        setToken('session-active');
        setUsername(loginForm.username);
        localStorage.setItem('jwt_token', 'session-active');
        localStorage.setItem('username', loginForm.username);
        localStorage.setItem('user_id', currentId);
        setAuthMessage({ type: 'success', text: `Hoş geldin @${loginForm.username}!` });
        setLoginForm({ username: '', password: '' });
      } else {
        const errorMsg = await parseErrorMessage(res, 'Giriş başarısız.');
        setAuthMessage({ type: 'error', text: errorMsg });
      }
    } catch (err) {
      setAuthMessage({ type: 'error', text: 'Sunucuya bağlanılamadı.' });
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setAuthMessage(null);
    try {
      const res = await fetch(`${API_BASE_URL}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(registerForm),
      });
      if (res.ok) {
        setAuthMessage({ type: 'success', text: 'Kayıt başarılı! Şimdi giriş yapabilirsiniz.' });
        setRegisterForm({ username: '', password: '' });
        setActiveTab('login');
      } else {
        const errorMsg = await parseErrorMessage(res, 'Kayıt başarısız.');
        setAuthMessage({ type: 'error', text: errorMsg });
      }
    } catch (err) {
      setAuthMessage({ type: 'error', text: 'Sunucuya bağlanılamadı.' });
    }
  };

  const handleCreateTweet = async (e) => {
    e.preventDefault();
    if (!token) {
      alert('Tweet atmak için lütfen giriş yapınız.');
      return;
    }
    try {
      const res = await fetch(`${API_BASE_URL}/tweet`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          content: tweetContent,
          userId: parseInt(storedUserId, 10),
        }),
      });

      if (res.ok) {
        setTweetContent('');
        fetchUserTweets(userId);
      } else {
        const msg = await parseErrorMessage(res, 'Tweet atılamadı.');
        alert(`Hata: ${msg}`);
      }
    } catch (err) {
      alert('Sunucu hatası.');
    }
  };

  const handleDeleteTweet = async (tweetId) => {
    if (!window.confirm('Bu tweeti silmek istediğinize emin misiniz?')) return;
    try {
      const res = await fetch(`${API_BASE_URL}/tweet/${tweetId}?userId=${storedUserId}`, {
        method: 'DELETE',
      });
      if (res.ok) {
        fetchUserTweets(userId);
      } else {
        const msg = await parseErrorMessage(res, 'Tweet silinemedi.');
        alert(`Hata: ${msg}`);
      }
    } catch (err) {
      alert('Sunucu hatası.');
    }
  };

  const handleLikeTweet = async (tweetId) => {
    try {
      const res = await fetch(`${API_BASE_URL}/like`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          tweetId: tweetId,
          userId: parseInt(storedUserId, 10),
        }),
      });
      if (res.ok) {
        fetchUserTweets(userId);
      } else {
        const msg = await parseErrorMessage(res, 'Beğenilemedi.');
        alert(`Bilgi: ${msg}`);
      }
    } catch (err) {
      alert('Sunucu hatası.');
    }
  };

  const handleRetweet = async (tweetId) => {
    try {
      const res = await fetch(`${API_BASE_URL}/retweet`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          tweetId: tweetId,
          userId: parseInt(storedUserId, 10),
        }),
      });
      if (res.ok) {
        fetchUserTweets(userId);
      } else {
        const msg = await parseErrorMessage(res, 'Retweet yapılamadı.');
        alert(`Bilgi: ${msg}`);
      }
    } catch (err) {
      alert('Sunucu hatası.');
    }
  };

  const handleCreateComment = async (tweetId) => {
    if (!commentContent.trim()) return;
    try {
      const res = await fetch(`${API_BASE_URL}/comment`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          tweetId: tweetId,
          userId: parseInt(storedUserId, 10),
          content: commentContent,
        }),
      });
      if (res.ok) {
        setCommentContent('');
        setActiveCommentTweetId(null);
        fetchUserTweets(userId);
      } else {
        const msg = await parseErrorMessage(res, 'Yorum eklenemedi.');
        alert(`Hata: ${msg}`);
      }
    } catch (err) {
      alert('Sunucu hatası.');
    }
  };

  const handleLogout = () => {
    setToken('');
    setUsername('');
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('username');
    localStorage.removeItem('user_id');
    setAuthMessage({ type: 'info', text: 'Çıkış yapıldı.' });
  };

  return (
      <div className="twitter-app">
        {/* Header */}
        <header className="app-header">
          <div className="header-container">
            <div className="logo-section">
              <svg className="twitter-logo" viewBox="0 0 24 24" aria-hidden="true">
                <path d="M23.643 4.937c-.835.37-1.732.62-2.675.733.962-.576 1.7-1.49 2.048-2.578-.9.534-1.897.922-2.958 1.13-.85-.904-2.06-1.47-3.4-1.47-2.572 0-4.658 2.086-4.658 4.66 0 .364.042.718.12 1.06-3.873-.195-7.304-2.05-9.602-4.868-.4.69-.63 1.49-.63 2.342 0 1.616.823 3.043 2.072 3.878-.764-.025-1.482-.234-2.11-.583v.06c0 2.257 1.605 4.14 3.737 4.568-.392.106-.803.162-1.227.162-.3 0-.593-.028-.877-.082.593 1.85 2.313 3.198 4.352 3.234-1.595 1.25-3.604 1.995-5.786 1.995-.376 0-.747-.022-1.112-.065 2.062 1.323 4.51 2.093 7.14 2.093 8.57 0 13.255-7.098 13.255-13.254 0-.2-.005-.402-.014-.602.91-.658 1.7-1.477 2.323-2.41z"></path>
              </svg>
              <h1>Twitter Clone</h1>
            </div>

            <div className="header-actions">
              <button className="btn-cors-info" onClick={() => setShowCorsModal(!showCorsModal)}>
                🌐 CORS Bilgisi
              </button>
              {username ? (
                  <div className="user-profile">
                    <span>@{username} (ID: #{storedUserId})</span>
                    <button onClick={handleLogout} className="btn-logout">Çıkış</button>
                  </div>
              ) : null}
            </div>
          </div>
        </header>

        {/* CORS Bilgi Modal */}
        {showCorsModal && (
            <div className="cors-modal-overlay" onClick={() => setShowCorsModal(false)}>
              <div className="cors-modal-card" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">
                  <h3>🌐 CORS & Mimari Bilgisi</h3>
                  <button className="btn-close" onClick={() => setShowCorsModal(false)}>✕</button>
                </div>
                <p><strong>İstek Kaynağı:</strong> <code>http://localhost:3200</code> (React UI)</p>
                <p><strong>Hedef API:</strong> <code>{API_BASE_URL}</code> (Spring Boot Backend)</p>
                <p>
                  <strong>Mimari:</strong> Spring Security & <code>GlobalExceptionHandler</code> yapısı aktif.
                  İş mantığı hataları <code>ApiException</code> olarak fırlatılır.
                </p>
                <div className={`status-badge ${corsStatus === 'BAŞARILI' ? 'success' : 'error'}`}>
                  Durum: {corsStatus}
                </div>
              </div>
            </div>
        )}

        {/* Main Grid */}
        <main className="main-content">
          <div className="content-grid">
            {/* Sol Kolon: Akış ve Tweet Oluşturma */}
            <section className="feed-column">
              <div className="tweet-composer">
                <div className="composer-avatar">
                  {username ? username.charAt(0).toUpperCase() : '?'}
                </div>
                <form onSubmit={handleCreateTweet} className="composer-form">
                <textarea
                    rows="3"
                    maxLength="280"
                    value={tweetContent}
                    onChange={(e) => setTweetContent(e.target.value)}
                    placeholder={token ? "Neler oluyor?" : "Tweet atmak için sağ taraftan giriş yapınız."}
                    disabled={!token}
                />
                  <div className="composer-footer">
                    <span className="char-count">{280 - tweetContent.length}</span>
                    <button type="submit" className="btn-tweet" disabled={!token || !tweetContent.trim()}>
                      Tweetle
                    </button>
                  </div>
                </form>
              </div>

              <div className="feed-header">
                <h2>Anasayfa / Tweetler</h2>
              </div>

              {loading && <div className="loading">Tweetler yükleniyor...</div>}

              {error && (
                  <div className="error-box">
                    <p><strong>Hata:</strong> {error}</p>
                  </div>
              )}

              {!loading && !error && tweets.length === 0 && (
                  <div className="empty-box">User ID #{userId} için henüz tweet bulunamadı.</div>
              )}

              <div className="tweets-list">
                {tweets.map((tweet) => {
                  const authorName = tweet.user ? tweet.user.username : `User#${tweet.userId}`;
                  const initial = authorName.charAt(0).toUpperCase();
                  const isOwner = String(tweet.userId) === String(storedUserId);

                  return (
                      <article key={tweet.id} className="tweet-card">
                        <div className="avatar-circle">{initial}</div>
                        <div className="tweet-body">
                          <div className="tweet-header">
                            <span className="tweet-author">@{authorName}</span>
                            <span className="tweet-date">
                          {tweet.createdAt
                              ? new Date(tweet.createdAt).toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' })
                              : 'Şimdi'}
                        </span>
                            {isOwner && token && (
                                <button
                                    className="btn-delete-tweet"
                                    title="Tweeti Sil"
                                    onClick={() => handleDeleteTweet(tweet.id)}
                                >
                                  🗑️
                                </button>
                            )}
                          </div>
                          <p className="tweet-text">{tweet.content}</p>
                          <div className="tweet-meta">
                            <button
                                className="meta-action-btn"
                                onClick={() => setActiveCommentTweetId(activeCommentTweetId === tweet.id ? null : tweet.id)}
                            >
                              💬 {tweet.comments ? tweet.comments.length : 0}
                            </button>
                            <button className="meta-action-btn" onClick={() => handleLikeTweet(tweet.id)}>
                              ❤️ {tweet.likes ? tweet.likes.length : 0}
                            </button>
                            <button className="meta-action-btn" onClick={() => handleRetweet(tweet.id)}>
                              🔄 {tweet.retweets ? tweet.retweets.length : 0}
                            </button>
                            <span className="tweet-id">#{tweet.id}</span>
                          </div>

                          {/* Yorum Yapma / Listeleme Alanı */}
                          {activeCommentTweetId === tweet.id && (
                              <div className="comment-section">
                                <div className="comment-input-box">
                                  <input
                                      type="text"
                                      placeholder="Yanıtınızı yazın..."
                                      value={commentContent}
                                      onChange={(e) => setCommentContent(e.target.value)}
                                      disabled={!token}
                                  />
                                  <button
                                      className="btn-small-primary"
                                      onClick={() => handleCreateComment(tweet.id)}
                                      disabled={!token || !commentContent.trim()}
                                  >
                                    Yanıtla
                                  </button>
                                </div>
                                {tweet.comments && tweet.comments.length > 0 && (
                                    <div className="comment-list">
                                      {tweet.comments.map((c) => (
                                          <div key={c.id} className="comment-item">
                                            <strong>@User#{c.userId}:</strong> {c.content}
                                          </div>
                                      ))}
                                    </div>
                                )}
                              </div>
                          )}
                        </div>
                      </article>
                  );
                })}
              </div>
            </section>

            {/* Sağ Kolon: Arama & Auth Paneli */}
            <aside className="sidebar-column">
              {/* Kullanıcı Tweet Filtresi */}
              <div className="card">
                <h3>🔍 Kullanıcı Tweeti Ara</h3>
                <form onSubmit={handleFetchClick} className="search-form">
                  <input
                      type="number"
                      value={userId}
                      onChange={(e) => setUserId(e.target.value)}
                      placeholder="User ID (ör: 1)"
                      min="1"
                  />
                  <button type="submit" className="btn-primary">Getir</button>
                </form>
                <div className="endpoint-hint">
                  Endpoint: <code>/tweet/findByUserId?userId={userId}</code>
                </div>
              </div>

              {/* Oturum Bilgisi / Aktif ID Ayarı */}
              {token && (
                  <div className="card">
                    <h3>👤 Aktif Simüle Edilen Kullanıcı</h3>
                    <div className="active-user-setting">
                      <label>User ID (İstek atan id):</label>
                      <input
                          type="number"
                          value={storedUserId}
                          onChange={(e) => {
                            setStoredUserId(e.target.value);
                            localStorage.setItem('user_id', e.target.value);
                          }}
                          min="1"
                      />
                    </div>
                  </div>
              )}

              {/* Giriş / Kayıt Paneli */}
              {!token && (
                  <div className="card auth-card">
                    <div className="auth-tabs">
                      <button
                          className={`tab-btn ${activeTab === 'login' ? 'active' : ''}`}
                          onClick={() => setActiveTab('login')}
                      >
                        Giriş Yap
                      </button>
                      <button
                          className={`tab-btn ${activeTab === 'register' ? 'active' : ''}`}
                          onClick={() => setActiveTab('register')}
                      >
                        Kayıt Ol
                      </button>
                    </div>

                    {authMessage && (
                        <div className={`auth-alert ${authMessage.type}`}>
                          {authMessage.text}
                        </div>
                    )}

                    {activeTab === 'login' ? (
                        <form onSubmit={handleLogin} className="form-stack">
                          <input
                              type="text"
                              placeholder="Kullanıcı Adı"
                              value={loginForm.username}
                              onChange={(e) => setLoginForm({ ...loginForm, username: e.target.value })}
                              required
                          />
                          <input
                              type="password"
                              placeholder="Şifre"
                              value={loginForm.password}
                              onChange={(e) => setLoginForm({ ...loginForm, password: e.target.value })}
                              required
                          />
                          <button type="submit" className="btn-secondary">Giriş Yap</button>
                        </form>
                    ) : (
                        <form onSubmit={handleRegister} className="form-stack">
                          <input
                              type="text"
                              placeholder="Kullanıcı Adı"
                              value={registerForm.username}
                              onChange={(e) => setRegisterForm({ ...registerForm, username: e.target.value })}
                              required
                          />
                          <input
                              type="password"
                              placeholder="Şifre (en az 6 karakter)"
                              value={registerForm.password}
                              onChange={(e) => setRegisterForm({ ...registerForm, password: e.target.value })}
                              required
                          />
                          <button type="submit" className="btn-secondary">Kayıt Ol</button>
                        </form>
                    )}
                  </div>
              )}
            </aside>
          </div>
        </main>
      </div>
  );
}

export default App;
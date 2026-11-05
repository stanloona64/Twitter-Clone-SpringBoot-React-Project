# Twitter Clone

## Gereksinimler

- Java 17 veya üzeri
- Node.js 20+
- PostgreSQL (`localhost:5432`)

## Veritabanını hazırlama

PostgreSQL'de `twitter_db` veritabanı oluşturun. PostgreSQL kurulumunda belirlediğiniz `postgres` kullanıcısının parolasını, backend'i başlatacağınız PowerShell terminalinde ortam değişkeni olarak tanımlayın:

```powershell
$env:DATABASE_PASSWORD = "PostgreSQL parolanız"
```

Parolayı kaynak koduna veya `application.properties` dosyasına koymayın.

## Çalıştırma

Backend'i aynı PowerShell terminalinde başlatın:

```powershell
cd twitter-backend
.\mvnw.cmd spring-boot:run
```

API `http://localhost:3000` adresinde çalışır. Bu terminali açık bırakın.

Ayrı bir terminalde frontend'i başlatın:

```powershell
cd twitter-client
npm install
npm start
```

Arayüz `http://localhost:3200` adresinde açılır ve backend'e `VITE_API_URL` üzerinden bağlanır. İlk çalıştırmada Maven ve npm bağımlılıklarını indirmek için internet bağlantısı gerekir.

`spring.jpa.hibernate.ddl-auto=update` mevcut veritabanı tablolarını koruyarak eksikleri günceller. SQL sorgularını backend terminalinde görmek için `spring.jpa.show-sql=true` ayarı etkindir.

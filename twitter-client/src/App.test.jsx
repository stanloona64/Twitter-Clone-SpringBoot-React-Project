import { render, screen } from '@testing-library/react';
import { beforeEach, expect, test, vi } from 'vitest';
import App from './App';

beforeEach(() => {
  // Backend açık olmadan da test çalışsın diye fetch'i taklit ediyoruz.
  global.fetch = vi.fn(() =>
    Promise.resolve({ ok: true, json: () => Promise.resolve([]) })
  );
});

test('uygulama başlığı ve giriş formu görünüyor', async () => {
  render(<App />);
  expect(screen.getByRole('heading', { name: /twitter clone/i })).toBeInTheDocument();
  expect(await screen.findByText(/henüz tweet bulunamadı/i)).toBeInTheDocument();
});

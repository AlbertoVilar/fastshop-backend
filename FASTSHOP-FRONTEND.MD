# FastShop Frontend — Guia de Início Rápido

Este documento reúne o código da página de entrada (Home) que você testou, e orienta como iniciar um projeto frontend (HTML estático ou React) usando esse mesmo visual.

## Visão Geral
- Estrutura moderna com `Header`, `Hero`, `Busca`, `Chips`, `Grid` de produtos, `Card`, `CTA` e `Footer`.
- Tipografia com Google Fonts (`Inter`) e tema via variáveis CSS (`:root`).
- Layout responsivo (grade 3-2-1 colunas), foco em legibilidade e contraste.
- Pronto para consumir APIs do FastShop (`/products`, `/categories`).

---

## HTML Estático — Inline CSS (copiar e colar)
Use este arquivo como landing estática. Basta salvar como `index.html` e abrir em um servidor estático (ex.: JDK `jwebserver`, `live-server`, ou qualquer outro).

```html
<!doctype html>
<html lang="pt-BR">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>FastShop — Ofertas, Produtos e Categorias</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
      :root { --bg:#0b0f1a; --surface:#11162a; --muted:#8aa0b4; --text:#eaf2f9; --primary:#4f8cff; --accent:#7c5cff; --border:#1b2340; --radius:16px; }
      body { margin:0; font-family:'Inter',system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif; background: radial-gradient(1200px 500px at 20% -10%, #0f1629 0%, #0b0f1a 60%), radial-gradient(900px 400px at 110% -20%, #121a33 0%, #0b0f1a 50%); color:var(--text); }
      .container { max-width:1120px; margin:0 auto; padding:0 24px; }
      .site-header { position:sticky; top:0; z-index:20; background: rgba(11,15,26,0.6); backdrop-filter: blur(10px); border-bottom:1px solid var(--border); }
      .header-content { display:grid; grid-template-columns:auto 1fr auto; align-items:center; gap:24px; padding:18px 0; }
      .logo { color:var(--text); text-decoration:none; font-weight:700; font-size:20px; }
      .nav { display:flex; gap:18px; }
      .nav a { color:var(--muted); text-decoration:none; font-weight:500; padding:10px 12px; border-radius:8px; }
      .nav a:hover { color:var(--text); background: rgba(124,92,255,0.10); }
      .header-actions { display:flex; gap:12px; }
      .btn-primary, .btn-outline { display:inline-flex; align-items:center; justify-content:center; height:40px; padding:0 16px; border-radius:12px; font-weight:600; text-decoration:none; }
      .btn-primary { background: linear-gradient(135deg, var(--primary), var(--accent)); color:#fff; }
      .btn-outline { border:1px solid var(--border); color:var(--text); background: rgba(17,22,42,0.6); }
      .hero { padding:64px 0 28px; }
      .hero h1 { font-size: clamp(28px, 4.5vw, 48px); line-height:1.12; margin:0 0 16px; }
      .hero p { margin:0 0 24px; color:var(--muted); }
      .search { display:grid; grid-template-columns:1fr auto; gap:12px; max-width:720px; }
      .search input { background: rgba(17,22,42,0.9); border:1px solid var(--border); color:var(--text); height:46px; padding:0 14px; border-radius:14px; outline:none; }
      .categories-chip { display:flex; gap:10px; margin-top:18px; flex-wrap:wrap; }
      .chip { display:inline-flex; align-items:center; height:34px; padding:0 12px; border:1px solid var(--border); color:var(--muted); border-radius:999px; text-decoration:none; background: rgba(17,22,42,0.6); }
      .chip:hover { color:var(--text); }
      .section { padding:18px 0 12px; }
      .section-header { display:flex; justify-content:space-between; align-items:end; padding-bottom:8px; }
      .grid { display:grid; grid-template-columns:repeat(3,1fr); gap:18px; }
      @media (max-width:1080px){ .grid{ grid-template-columns:repeat(2,1fr);} }
      @media (max-width:720px){ .grid{ grid-template-columns:1fr;} }
      .card { background: linear-gradient(180deg, rgba(20,26,45,0.95) 0%, rgba(17,22,42,0.95) 100%); border:1px solid var(--border); border-radius:var(--radius); overflow:hidden; }
      .card-media { aspect-ratio:16/10; overflow:hidden; }
      .card-media img { width:100%; height:100%; object-fit:cover; }
      .card-body { padding:16px; }
      .card h3 { margin:0 0 8px; font-size:18px; }
      .price { margin:0 0 8px; font-weight:700; color:#b8d0ff; }
      .desc { margin:0 0 12px; color:var(--muted); }
      .card-actions { display:flex; gap:10px; }
      .cta { margin:20px 0 40px; }
      .cta-content { background: linear-gradient(180deg, rgba(20,26,45,0.95) 0%, rgba(17,22,42,0.95) 100%); border:1px solid var(--border); border-radius:var(--radius); padding:28px; display:grid; grid-template-columns:1fr auto; align-items:center; gap:16px; }
      .cta-form { display:grid; grid-template-columns:1fr auto; gap:12px; }
      .cta-form input { background: rgba(17,22,42,0.9); border:1px solid var(--border); color:var(--text); height:46px; padding:0 14px; border-radius:14px; outline:none; }
      .site-footer { border-top:1px solid var(--border); background: rgba(11,15,26,0.75); }
      .footer-content { display:grid; grid-template-columns:repeat(3,1fr); gap:18px; padding:24px 0; }
      @media (max-width:720px){ .footer-content{ grid-template-columns:1fr; } }
      .footer-content h4 { margin:0 0 10px; }
      .footer-content p, .footer-content ul { margin:0; color:var(--muted); }
      .footer-content ul { list-style:none; padding:0; }
      .footer-content a { color:var(--muted); text-decoration:none; }
      .footer-bottom { padding:16px 0; border-top:1px solid var(--border); color:var(--muted); }
    </style>
  </head>
  <body>
    <header class="site-header">
      <div class="container header-content">
        <a class="logo" href="/">FastShop</a>
        <nav class="nav">
          <a href="#produtos">Produtos</a>
          <a href="#categorias">Categorias</a>
          <a href="#ofertas">Ofertas</a>
          <a href="#contato">Contato</a>
        </nav>
        <div class="header-actions">
          <a class="btn-outline" href="#login">Entrar</a>
          <a class="btn-primary" href="#carrinho">Carrinho</a>
        </div>
      </div>
    </header>

    <main>
      <!-- Hero -->
      <section class="hero">
        <div class="container hero-content">
          <h1>Descubra ofertas incríveis para o seu dia a dia</h1>
          <p>Eletrônicos, moda, casa, mercado e muito mais. Compre com rapidez e segurança.</p>
          <form class="search" action="#" method="get">
            <input type="text" name="q" placeholder="Busque por produtos, marcas ou categorias" aria-label="Buscar" />
            <button class="btn-primary" type="submit">Buscar</button>
          </form>
          <div class="categories-chip" id="categorias">
            <a href="#" class="chip">Eletrônicos</a>
            <a href="#" class="chip">Casa e Decoração</a>
            <a href="#" class="chip">Moda</a>
            <a href="#" class="chip">Esporte</a>
            <a href="#" class="chip">Mercado</a>
            <a href="#" class="chip">Pets</a>
          </div>
        </div>
      </section>

      <!-- Destaques / Produtos -->
      <section class="section" id="produtos">
        <div class="container section-header">
          <h2>Produtos em destaque</h2>
          <a href="#" class="link">Ver todos</a>
        </div>
        <div class="container grid">
          <article class="card">
            <div class="card-media">
              <img src="https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?q=80&w=1200&auto=format&fit=crop" alt="Smartphone" />
            </div>
            <div class="card-body">
              <h3>Smartphone X Pro</h3>
              <p class="price">R$ 2.499,00</p>
              <p class="desc">Tela 6.5", 128GB, Câmera dupla</p>
              <div class="card-actions">
                <a class="btn-outline" href="#">Detalhes</a>
                <a class="btn-primary" href="#">Adicionar</a>
              </div>
            </div>
          </article>

          <article class="card">
            <div class="card-media">
              <img src="https://images.unsplash.com/photo-1517336714731-489689fd1ca8?q=80&w=1200&auto=format&fit=crop" alt="Notebook" />
            </div>
            <div class="card-body">
              <h3>Notebook Flex 14</h3>
              <p class="price">R$ 3.899,00</p>
              <p class="desc">Intel i7, 16GB RAM, SSD 512GB</p>
              <div class="card-actions">
                <a class="btn-outline" href="#">Detalhes</a>
                <a class="btn-primary" href="#">Adicionar</a>
              </div>
            </div>
          </article>

          <article class="card">
            <div class="card-media">
              <img src="https://images.unsplash.com/photo-1512499617640-c2f999098c4b?q=80&w=1200&auto=format&fit=crop" alt="Fone de Ouvido" />
            </div>
            <div class="card-body">
              <h3>Fone Wireless Hifi</h3>
              <p class="price">R$ 499,00</p>
              <p class="desc">Cancelamento de ruído, 30h bateria</p>
              <div class="card-actions">
                <a class="btn-outline" href="#">Detalhes</a>
                <a class="btn-primary" href="#">Adicionar</a>
              </div>
            </div>
          </article>

          <article class="card">
            <div class="card-media">
              <img src="https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=1200&auto=format&fit=crop" alt="Tênis" />
            </div>
            <div class="card-body">
              <h3>Tênis RunMax</h3>
              <p class="price">R$ 299,00</p>
              <p class="desc">Conforto e performance para o dia a dia</p>
              <div class="card-actions">
                <a class="btn-outline" href="#">Detalhes</a>
                <a class="btn-primary" href="#">Adicionar</a>
              </div>
            </div>
          </article>

          <article class="card">
            <div class="card-media">
              <img src="https://images.unsplash.com/photo-1505577078420-7c9fa3ee3eb2?q=80&w=1200&auto=format&fit=crop" alt="Cafeteira" />
            </div>
            <div class="card-body">
              <h3>Cafeteira Barista</h3>
              <p class="price">R$ 699,00</p>
              <p class="desc">Espresso cremoso na sua cozinha</p>
              <div class="card-actions">
                <a class="btn-outline" href="#">Detalhes</a>
                <a class="btn-primary" href="#">Adicionar</a>
              </div>
            </div>
          </article>

          <article class="card">
            <div class="card-media">
              <img src="https://images.unsplash.com/photo-1526328828355-54f8a1c89c62?q=80&w=1200&auto=format&fit=crop" alt="Poltrona" />
            </div>
            <div class="card-body">
              <h3>Poltrona SoftCloud</h3>
              <p class="price">R$ 899,00</p>
              <p class="desc">Design moderno e conforto premium</p>
              <div class="card-actions">
                <a class="btn-outline" href="#">Detalhes</a>
                <a class="btn-primary" href="#">Adicionar</a>
              </div>
            </div>
          </article>
        </div>
      </section>

      <!-- CTA -->
      <section class="cta" id="ofertas">
        <div class="container cta-content">
          <h2>Assine nossa newsletter e receba ofertas exclusivas</h2>
          <form class="cta-form" action="#" method="post">
            <input type="email" placeholder="Seu e-mail" aria-label="E-mail" required />
            <button class="btn-primary" type="submit">Assinar</button>
          </form>
        </div>
      </section>
    </main>

    <footer class="site-footer" id="contato">
      <div class="container footer-content">
        <div>
          <h4>FastShop</h4>
          <p>Seu marketplace moderno e funcional. Compre com confiança.</p>
        </div>
        <div>
          <h4>Institucional</h4>
          <ul>
            <li><a href="#">Sobre</a></li>
            <li><a href="#">Carreiras</a></li>
            <li><a href="#">Blog</a></li>
          </ul>
        </div>
        <div>
          <h4>Suporte</h4>
          <ul>
            <li><a href="#">Ajuda</a></li>
            <li><a href="#">Pedidos</a></li>
            <li><a href="#">Políticas</a></li>
          </ul>
        </div>
      </div>
      <div class="container footer-bottom">
        <small>© 2025 FastShop. Todos os direitos reservados.</small>
      </div>
    </footer>
  </body>
  </html>
```

### Servir rapidamente (HTML estático)
- JDK 21+: `jwebserver -p 8081 -d ./public` (ou a pasta onde o `index.html` está).
- Node: `npx serve` ou `npx live-server` na pasta do arquivo.

---

## Variante com CSS extraído (recomendado para projetos)
Se preferir manter o CSS em arquivo separado, use este `index.html` e crie `styles.css` com o conteúdo abaixo.

```html
<!doctype html>
<html lang="pt-BR">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>FastShop — Ofertas, Produtos e Categorias</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/styles.css">
  </head>
  <body><!-- mesmo conteúdo do body acima --></body>
  </html>
```

`styles.css`

```css
:root { --bg:#0b0f1a; --surface:#11162a; --muted:#8aa0b4; --text:#eaf2f9; --primary:#4f8cff; --accent:#7c5cff; --border:#1b2340; --radius:16px; }
body { margin:0; font-family:'Inter',system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif; background: radial-gradient(1200px 500px at 20% -10%, #0f1629 0%, #0b0f1a 60%), radial-gradient(900px 400px at 110% -20%, #121a33 0%, #0b0f1a 50%); color:var(--text); }
.container { max-width:1120px; margin:0 auto; padding:0 24px; }
.site-header { position:sticky; top:0; z-index:20; background: rgba(11,15,26,0.6); backdrop-filter: blur(10px); border-bottom:1px solid var(--border); }
.header-content { display:grid; grid-template-columns:auto 1fr auto; align-items:center; gap:24px; padding:18px 0; }
.logo { color:var(--text); text-decoration:none; font-weight:700; font-size:20px; }
.nav { display:flex; gap:18px; }
.nav a { color:var(--muted); text-decoration:none; font-weight:500; padding:10px 12px; border-radius:8px; }
.nav a:hover { color:var(--text); background: rgba(124,92,255,0.10); }
.header-actions { display:flex; gap:12px; }
.btn-primary, .btn-outline { display:inline-flex; align-items:center; justify-content:center; height:40px; padding:0 16px; border-radius:12px; font-weight:600; text-decoration:none; }
.btn-primary { background: linear-gradient(135deg, var(--primary), var(--accent)); color:#fff; }
.btn-outline { border:1px solid var(--border); color:var(--text); background: rgba(17,22,42,0.6); }
.hero { padding:64px 0 28px; }
.hero h1 { font-size: clamp(28px, 4.5vw, 48px); line-height:1.12; margin:0 0 16px; }
.hero p { margin:0 0 24px; color:var(--muted); }
.search { display:grid; grid-template-columns:1fr auto; gap:12px; max-width:720px; }
.search input { background: rgba(17,22,42,0.9); border:1px solid var(--border); color:var(--text); height:46px; padding:0 14px; border-radius:14px; outline:none; }
.categories-chip { display:flex; gap:10px; margin-top:18px; flex-wrap:wrap; }
.chip { display:inline-flex; align-items:center; height:34px; padding:0 12px; border:1px solid var(--border); color:var(--muted); border-radius:999px; text-decoration:none; background: rgba(17,22,42,0.6); }
.chip:hover { color:var(--text); }
.section { padding:18px 0 12px; }
.section-header { display:flex; justify-content:space-between; align-items:end; padding-bottom:8px; }
.grid { display:grid; grid-template-columns:repeat(3,1fr); gap:18px; }
@media (max-width:1080px){ .grid{ grid-template-columns:repeat(2,1fr);} }
@media (max-width:720px){ .grid{ grid-template-columns:1fr;} }
.card { background: linear-gradient(180deg, rgba(20,26,45,0.95) 0%, rgba(17,22,42,0.95) 100%); border:1px solid var(--border); border-radius:var(--radius); overflow:hidden; }
.card-media { aspect-ratio:16/10; overflow:hidden; }
.card-media img { width:100%; height:100%; object-fit:cover; }
.card-body { padding:16px; }
.card h3 { margin:0 0 8px; font-size:18px; }
.price { margin:0 0 8px; font-weight:700; color:#b8d0ff; }
.desc { margin:0 0 12px; color:var(--muted); }
.card-actions { display:flex; gap:10px; }
.cta { margin:20px 0 40px; }
.cta-content { background: linear-gradient(180deg, rgba(20,26,45,0.95) 0%, rgba(17,22,42,0.95) 100%); border:1px solid var(--border); border-radius:var(--radius); padding:28px; display:grid; grid-template-columns:1fr auto; align-items:center; gap:16px; }
.cta-form { display:grid; grid-template-columns:1fr auto; gap:12px; }
.cta-form input { background: rgba(17,22,42,0.9); border:1px solid var(--border); color:var(--text); height:46px; padding:0 14px; border-radius:14px; outline:none; }
.site-footer { border-top:1px solid var(--border); background: rgba(11,15,26,0.75); }
.footer-content { display:grid; grid-template-columns:repeat(3,1fr); gap:18px; padding:24px 0; }
@media (max-width:720px){ .footer-content{ grid-template-columns:1fr; } }
.footer-content h4 { margin:0 0 10px; }
.footer-content p, .footer-content ul { margin:0; color:var(--muted); }
.footer-content ul { list-style:none; padding:0; }
.footer-content a { color:var(--muted); text-decoration:none; }
.footer-bottom { padding:16px 0; border-top:1px solid var(--border); color:var(--muted); }
```

---

## Início Rápido com React (Vite)
Se quiser componentizar, este guia entrega uma base em React.

### Estrutura sugerida
```
fastshop-frontend/
  src/
    components/
      Header.jsx
      Hero.jsx
      Chips.jsx
      ProductCard.jsx
      ProductGrid.jsx
      CTA.jsx
      Footer.jsx
    App.jsx
    main.jsx
    styles.css
  index.html
```

### `index.html`
```html
<!doctype html>
<html lang="pt-BR">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>FastShop Frontend</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.jsx"></script>
  </body>
  </html>
```

### `src/styles.css`
Use o mesmo conteúdo de `styles.css` acima (variáveis e classes).

### `src/App.jsx`
```jsx
import './styles.css'
import Header from './components/Header'
import Hero from './components/Hero'
import Chips from './components/Chips'
import ProductGrid from './components/ProductGrid'
import CTA from './components/CTA'
import Footer from './components/Footer'

export default function App(){
  return (
    <>
      <Header />
      <main>
        <section className="hero">
          <div className="container hero-content">
            <Hero />
            <Chips />
          </div>
        </section>
        <section className="section" id="produtos">
          <div className="container section-header">
            <h2>Produtos em destaque</h2>
            <a href="#" className="link">Ver todos</a>
          </div>
          <div className="container">
            <ProductGrid />
          </div>
        </section>
        <section className="cta" id="ofertas">
          <div className="container cta-content">
            <CTA />
          </div>
        </section>
      </main>
      <Footer />
    </>
  )
}
```

### Componentes

`src/components/Header.jsx`
```jsx
export default function Header(){
  return (
    <header className="site-header">
      <div className="container header-content">
        <a className="logo" href="/">FastShop</a>
        <nav className="nav">
          <a href="#produtos">Produtos</a>
          <a href="#categorias">Categorias</a>
          <a href="#ofertas">Ofertas</a>
          <a href="#contato">Contato</a>
        </nav>
        <div className="header-actions">
          <a className="btn-outline" href="#login">Entrar</a>
          <a className="btn-primary" href="#carrinho">Carrinho</a>
        </div>
      </div>
    </header>
  )
}
```

`src/components/Hero.jsx`
```jsx
export default function Hero(){
  return (
    <>
      <h1>Descubra ofertas incríveis para o seu dia a dia</h1>
      <p>Eletrônicos, moda, casa, mercado e muito mais. Compre com rapidez e segurança.</p>
      <form className="search" action="#" method="get" onSubmit={(e)=>e.preventDefault()}>
        <input type="text" name="q" placeholder="Busque por produtos, marcas ou categorias" aria-label="Buscar" />
        <button className="btn-primary" type="submit">Buscar</button>
      </form>
    </>
  )
}
```

`src/components/Chips.jsx`
```jsx
export default function Chips(){
  const chips = ['Eletrônicos','Casa e Decoração','Moda','Esporte','Mercado','Pets']
  return (
    <div className="categories-chip" id="categorias">
      {chips.map(label => (
        <a key={label} href="#" className="chip">{label}</a>
      ))}
    </div>
  )
}
```

`src/components/ProductCard.jsx`
```jsx
export default function ProductCard({ product }){
  return (
    <article className="card">
      <div className="card-media">
        <img src={product.image} alt={product.name} loading="lazy" />
      </div>
      <div className="card-body">
        <h3>{product.name}</h3>
        <p className="price">{product.priceFormatted}</p>
        <p className="desc">{product.description}</p>
        <div className="card-actions">
          <a className="btn-outline" href="#">Detalhes</a>
          <a className="btn-primary" href="#">Adicionar</a>
        </div>
      </div>
    </article>
  )
}
```

`src/components/ProductGrid.jsx`
```jsx
import { useEffect, useState } from 'react'
import ProductCard from './ProductCard'

export default function ProductGrid(){
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(()=>{
    async function load(){
      try {
        setLoading(true)
        const res = await fetch('/products') // ajuste baseURL conforme seu backend
        const data = await res.json()
        const normalized = data.map(p => ({
          id: p.id,
          name: p.name || p.productName || 'Produto',
          description: p.description || '',
          priceFormatted: p.price ? `R$ ${Number(p.price).toLocaleString('pt-BR')}` : 'Preço sob consulta',
          image: p.imageUrl || 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?q=80&w=1200&auto=format&fit=crop'
        }))
        setItems(normalized)
      } catch (e){
        setError('Não foi possível carregar produtos')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  if (loading) return <p className="desc">Carregando...</p>
  if (error) return <p className="desc">{error}</p>

  return (
    <div className="grid">
      {items.map(item => (
        <ProductCard key={item.id} product={item} />
      ))}
    </div>
  )
}
```

`src/components/CTA.jsx`
```jsx
export default function CTA(){
  return (
    <>
      <h2>Assine nossa newsletter e receba ofertas exclusivas</h2>
      <form className="cta-form" action="#" method="post" onSubmit={(e)=>e.preventDefault()}>
        <input type="email" placeholder="Seu e-mail" aria-label="E-mail" required />
        <button className="btn-primary" type="submit">Assinar</button>
      </form>
    </>
  )
}
```

`src/components/Footer.jsx`
```jsx
export default function Footer(){
  return (
    <footer className="site-footer" id="contato">
      <div className="container footer-content">
        <div>
          <h4>FastShop</h4>
          <p>Seu marketplace moderno e funcional. Compre com confiança.</p>
        </div>
        <div>
          <h4>Institucional</h4>
          <ul>
            <li><a href="#">Sobre</a></li>
            <li><a href="#">Carreiras</a></li>
            <li><a href="#">Blog</a></li>
          </ul>
        </div>
        <div>
          <h4>Suporte</h4>
          <ul>
            <li><a href="#">Ajuda</a></li>
            <li><a href="#">Pedidos</a></li>
            <li><a href="#">Políticas</a></li>
          </ul>
        </div>
      </div>
      <div className="container footer-bottom">
        <small>© 2025 FastShop. Todos os direitos reservados.</small>
      </div>
    </footer>
  )
}
```

`src/main.jsx`
```jsx
import { createRoot } from 'react-dom/client'
import App from './App'

createRoot(document.getElementById('root')).render(<App />)
```

### Rodar localmente (React)
- Node 18+: `npm create vite@latest fastshop-frontend -- --template react`
- Dentro do diretório: `npm install && npm run dev`
- Ajuste `fetch('/products')` para a URL do backend se necessário (ex.: `http://localhost:8080/products`).

---

## Integração com Backend
- Endpoints esperados: `/products`, `/categories`, `/auth/login` etc.
- Habilite CORS no backend para o `localhost` do frontend.
- Se usar proxy no Vite, configure `vite.config.js` com `server.proxy` apontando para a API.

---

## Boas Práticas
- Acessibilidade: `aria-label`, foco visível, texto alternativo nas imagens.
- Performance: imagens com `loading="lazy"`, minificação e cache.
- Temas: mantenha variáveis no `:root` para dark/light e branding.
- Versionamento: mantenha protótipos HTML/CSS fora do commit se desejar (via `.gitignore`).

---

## Servidor estático rápido (sem framework)
Para validar apenas HTML/CSS:
- JDK 21+: `jwebserver -p 8081 -d ./public` e abra `http://127.0.0.1:8081/`.
- Ou `npx serve` na pasta com `index.html`.

---

Qualquer ajuste de cores, tipografia, espaçamento ou grid, posso adaptar aqui e atualizar os trechos de código.
const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const app = express();
const port = 8083;

app.use('/dev-api', createProxyMiddleware({
  target: 'http://localhost:8989',
  changeOrigin: true,
  pathRewrite: { '^/dev-api': '' }
}));

app.use('/blog-api', createProxyMiddleware({
  target: 'http://localhost:7777',
  changeOrigin: true,
  pathRewrite: { '^/blog-api': '' }
}));

app.use(express.static('dist'));

app.use((req, res) => {
  res.sendFile(__dirname + '/dist/index.html');
});

app.listen(port, () => {
  console.log(`Admin UI at http://localhost:${port}`);
});

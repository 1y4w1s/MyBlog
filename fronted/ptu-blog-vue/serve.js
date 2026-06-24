var http = require('http');
var fs = require('fs');
var path = require('path');

var dist = path.join(__dirname, 'dist');
var port = 8888;
var staticExts = ['.html', '.js', '.css', '.png', '.jpg', '.jpeg', '.gif', '.ico', '.ttf', '.woff', '.woff2', '.svg'];

http.createServer(function (req, res) {
    var url = req.url.split('?')[0];
    var ext = path.extname(url);
    var filePath = path.join(dist, url === '/' ? 'index.html' : url);

    if (staticExts.indexOf(ext) !== -1) {
        var mime = {
            '.html': 'text/html',
            '.js': 'application/javascript',
            '.css': 'text/css',
            '.png': 'image/png',
            '.jpg': 'image/jpeg',
            '.jpeg': 'image/jpeg',
            '.gif': 'image/gif',
            '.ico': 'image/x-icon',
            '.ttf': 'font/ttf',
            '.woff': 'font/woff',
            '.woff2': 'font/woff2',
            '.svg': 'image/svg+xml'
        };
        fs.readFile(filePath, function (err, data) {
            if (err) {
                res.writeHead(404);
                res.end('Not found');
                return;
            }
            res.writeHead(200, { 'Content-Type': mime[ext] || 'application/octet-stream' });
            res.end(data);
        });
    } else {
        var indexPath = path.join(dist, 'index.html');
        fs.readFile(indexPath, function (err, data) {
            if (err) {
                res.writeHead(404);
                res.end('Not found');
                return;
            }
            res.writeHead(200, { 'Content-Type': 'text/html' });
            res.end(data);
        });
    }
}).listen(port, function () {
    console.log('Frontend serving at http://localhost:' + port);
});

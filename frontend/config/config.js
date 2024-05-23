const fs = require('fs');

fs.copyFile('build/index.html', 'build/www/index.html', (err) => {
    if (err) {
        throw err;
    }
    console.log('Copied index.html!');
});
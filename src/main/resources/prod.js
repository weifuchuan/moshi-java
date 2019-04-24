const fs = require('fs');

const text = fs.readFileSync("./jboot.properties").toString();

const lines = text.split("\n");

const text1 = lines.map(line => {
    if (line.startsWith("jboot.app.mode=")) return "jboot.app.mode=prod";
    if (line.startsWith("undertow.devMode=")) return "undertow.devMode=false";
    if (line.startsWith("easyrec")) return line;
    if (line.startsWith("host=")) return "host=http://123.207.28.107";
    if (/123\.207\.28\.107/.test(line)) return line.replace(/123\.207\.28\.107/g, "127.0.0.1");
    return line;
}).reduce((p, c) => p + "\n" + c, '');

fs.writeFileSync("./jboot.properties", text1);
fs.writeFileSync("./jboot.properties.bak", text);

const imText = fs.readFileSync("./im/config.properties").toString();

const imText1 = imText.split("\n").map(line => {
    if (line.startsWith("mode=")) return "mode=prod";
    if (/123\.207\.28\.107/.test(line)) return line.replace(/123\.207\.28\.107/g, "127.0.0.1");
    return line;
}).reduce((p, c) => p + "\n" + c, '');

fs.writeFileSync("./im/config.properties", imText1);
fs.writeFileSync("./im/config.properties.bak", imText);
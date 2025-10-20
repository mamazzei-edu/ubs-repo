const bonjour = require('bonjour')(); // Inicializa o Bonjour

const http = require('http');
HOSTIP= '';
const express = require('express');
const app = express();

app.use(express.urlencoded({ extended: true }));


app.get('/', (req, res) => {
  res.send(`
    <DOCTYPE html>
    <html>
      <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">  
        <title>Servidor NSD</title>
        </head>
        <body>
          <h1>Informe o endereço IP do atribuído ao servidor</h1>
          <form action="/set-ip" method="post">
            <label for="ip">Endereço IP:</label>
            <input type="text" id="ip" name="ip" required>
            <button type="submit">Enviar</button>
          </form>
        </body>
    </html>
  `);
});

app.post('/set-ip', (req, res) => {
  const { ip } = req.body;
  if (ip) {
    HOSTIP = ip;
    res.send(`Endereço IP do servidor atualizado para: ${HOSTIP}`);
  bonjour.publish({ 
    name: 'UBSAuth1', 
    type: 'http', 
    host: '10.67.173.21', // Esse endereço é o que deve ser atualizado quando da instalação no cliente
    protocol: 'tcp',
    port: PORT,
    txt: { authPort: PORT_AUTH.toString(), apiPort: PORT_API.toString(), hostIP: HOSTIP } 
  });


  } else {
    res.status(400).send('Endereço IP inválido');
  }
});
//
/** const server = http.createServer((req, res) => {
  res.writeHead(200, { 'Content-Type': 'text/plain' });
  res.end('Servidor NSD em execução\n');
});
*/ 


const PORT = 3000;
const PORT_AUTH = 8080;
const PORT_API = 8081;
 // Porta para o serviço de autenticação
app.listen(PORT, () => {
  console.log(`Servidor rodando na porta ${PORT}`);
});

process.on('SIGINT', () => {
  bonjour.unpublishAll(() => {
    bonjour.destroy();
    process.exit();
  });
});

<h1 style="color: #5e9ca0; text-align: center;"><span style="color: #99ccff;">YoTeReparo</span></h1>
<h3 style="color: #5e9ca0; text-align: center;"><span style="color: #000000;"><em>Landing Page</em></span> <span style="color: #000000;">:)</span></h3>
<hr />
<h3 style="color: #2e6c80;"><span style="color: #000000;">Endpoints disponibles</span></h3>
<p><strong><span style="color: #000000;">Entity: Usuario</span></strong></p>
<ul>
    <li><strong><span style="color: #000000;">/yotereparo/users/</span></strong></li>
</ul>
<p style="padding-left: 30px;"><em>GET</em>: Devuelve la lista de todos los usuarios registrados. (application/json)</p>
<p style="padding-left: 30px;"><em>POST</em>: Si no existe, crea y persiste un usuario con atributos mandatorios: {id, nombre, apellido, email, contrasena} (application/json)</p>
<ul>
    <li><strong>/yotereparo/users/{id}</strong></li>
</ul>
<p style="padding-left: 30px;"><em>GET</em>: Devuelve el usuario {id} si es que existe. (application/json)</p>
<p style="padding-left: 30px;"><em>PUT</em>: Actualiza los atributos del usuario {id} si es que existe, mandatorios: {id, nombre, apellido, email, contrasena}. Devuelve el usuario actualizado. (application/json)</p>
<p style="padding-left: 30px;"><em>DELETE</em>: Elimina el usuario {id} si es que existe. (application/json)</p>
<ul>
    <li><strong>/yotereparo/users/{id}/photo</strong></li>
</ul>
<p style="padding-left: 30px;"><em>GET</em>: Devuelve la foto del usuario {id}. (image/png)</p>
<p style="padding-left: 30px;"><em>PUT</em>: Actualiza la foto (y el thumbnail generado a partir de esta) del usuario {id}. (application/json)</p>
<ul>
    <li><strong>/yotereparo/users/{id}/photo/thumbnail</strong></li>
</ul>
<p style="padding-left: 30px;"><em>GET</em>: Devuelve el thumbnail del usuario {id}. (image/png)</p>
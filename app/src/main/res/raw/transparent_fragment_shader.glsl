precision mediump float;
uniform sampler2D u_Texture;
varying vec2 v_TexCoordinate;
varying float v_transp;

void main()
{
  vec4 t = texture2D(u_Texture, v_TexCoordinate);
  t.a = t.a * v_transp;
  gl_FragColor = t;
}
precision mediump float;
uniform sampler2D u_Texture;
uniform sampler2D u_Map;
varying vec2 v_TexCoordinate;
varying vec2 v_Position;

void main()
{
  gl_FragColor = texture2D(u_Texture, v_TexCoordinate);
}
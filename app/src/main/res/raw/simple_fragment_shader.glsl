precision mediump float;
uniform sampler2D u_Texture;
varying vec2 v_TexCoordinate;
varying vec4 v_Color;

void main()
{
  vec4 t = texture2D(u_Texture, v_TexCoordinate);
  gl_FragColor = v_Color * sign(t.a);
}
precision mediump float;
uniform sampler2D u_Texture;
uniform sampler2D u_Map;
varying vec2 v_TexCoordinate;
varying vec2 v_Position;
varying vec4 v_RealPosition;
varying float v_Time;

void main()
{
  vec4 bgrColor = texture2D(u_Map, v_Position);
  float flag = 1.0 - bgrColor.g / 0.694;
  vec2 tc = vec2(v_TexCoordinate.x + 0.05 * sin ((v_Position.x + v_Position.y) * 5.0 + v_Time) * flag, v_TexCoordinate.y + 0.05 * cos ((v_Position.x + v_Position.y) * 5.0 + v_Time) * flag);
  gl_FragColor = texture2D(u_Texture, tc);
}
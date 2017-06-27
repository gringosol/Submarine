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
  bgrColor = vec4(1.0, 1.0, 1.0, 1.0) * (1.0 - bgrColor.g) * (0.4 * (sin((v_RealPosition.x + v_RealPosition.y) * 15.0 + v_Time * 2.0) + 1.0) + 0.1 * (sin(v_RealPosition.y * 50.0 + v_Time * 0.5) + 1.0));
  bgrColor.a = 0.0;
  gl_FragColor = texture2D(u_Texture, v_TexCoordinate) * 0.9 + bgrColor * 0.1;
}
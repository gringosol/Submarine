precision mediump float;
uniform sampler2D u_Texture;
uniform sampler2D u_Map;
varying vec2 v_TexCoordinate;
varying vec2 v_Position;
varying vec4 v_RealPosition;
varying float v_Time;

float random (in vec2 st) {
    return fract(sin(dot(st.xy,
                         vec2(12.9898,78.233)))
                 * 43758.5453123);
}

float noise (in vec2 st) {
    vec2 i = floor(st);
    vec2 f = fract(st);
    float a = random(i);
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));
    vec2 u = f*f*(3.0-2.0*f);
    return mix(a, b, u.x) +
            (c - a)* u.y * (1.0 - u.x) +
            (d - b) * u.x * u.y;
}

void main()
{
  vec4 bgrColor = texture2D(u_Map, v_Position);
  float flag = 1.0 - bgrColor.g / 0.694;
  /*bgrColor = vec4(1.0, 1.0, 1.0, 1.0) * (1.0 - bgrColor.g) * (0.4 * (sin((v_RealPosition.x + v_RealPosition.y) * 15.0 + v_Time * 2.0) + 1.0) + 0.1 * (sin(v_RealPosition.y * 50.0 + v_Time * 0.5) + 1.0));
  bgrColor.a = 0.0;*/



  vec2 st = (v_RealPosition.xy)/vec2(0.5, 0.5);
  st = vec2(st.x + 0.07 * sin ((v_Position.x + v_Position.y) * 5.0 + v_Time * 2.0) * flag, st.y + 0.08 * cos ((v_Position.x + v_Position.y) * 5.0 + v_Time * 2.0) * flag);
  vec2 pos = vec2(st*5.0);
  float n = noise(pos);

  vec2 tc = vec2(v_TexCoordinate.x + 0.05 * sin ((v_Position.x + v_Position.y) * 5.0 + v_Time) * flag, v_TexCoordinate.y + 0.05 * cos ((v_Position.x + v_Position.y) * 5.0 + v_Time) * flag);

  //bgrColor = vec4(1.0, 1.0, 1.0, 1.0) * (1.0 - bgrColor.g) * sin ((v_Position.x + v_Position.y) * 50.0 + v_Time) * sin ((v_Position.x - v_Position.y) * 50.0 + v_Time);
  //bgrColor.a = 0.0;


  gl_FragColor = texture2D(u_Texture, tc/*v_TexCoordinate*/) * 0.9 + 0.1 * flag * vec4(vec3(n), 1.0) * (0.5 * sin ((v_Position.x + v_Position.y) * 5.0 + v_Time) + 0.5)/*bgrColor * 0.2*/;
}
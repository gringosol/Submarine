precision mediump float;
uniform mat4 u_MTransform;
uniform mat4 u_MModel;
uniform float u_Time;
attribute vec4 a_Position;
attribute vec2 a_TexCoordinate;
varying vec2 v_TexCoordinate;
varying vec2 v_Position;
varying vec4 v_RealPosition;
varying float v_Time;

void main() {
  v_TexCoordinate = a_TexCoordinate;
  vec4 coord = u_MTransform * a_Position;
  v_Position.x = coord.x * 0.5 + 0.5;
  v_Position.y = coord.y * 0.5 + 0.5;
  v_RealPosition = u_MModel * a_Position;
  v_Time = u_Time;
  vec4 newPos = u_MTransform * a_Position;
  //newPos = vec4(newPos.x + 0.05 * sin ((newPos.x + newPos.y) * 5.0 + v_Time), newPos.y + 0.05 * cos ((newPos.x + newPos.y) * 5.0 + v_Time), newPos.z, newPos.w);
  gl_Position = newPos;
}
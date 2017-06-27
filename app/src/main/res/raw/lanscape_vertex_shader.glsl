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
  gl_Position =   u_MTransform * a_Position;
}
precision mediump float;
uniform mat4 u_MTransform;
attribute vec4 a_Position;
attribute vec2 a_TexCoordinate;
varying vec2 v_TexCoordinate;
varying vec2 v_Position;

void main() {
  v_TexCoordinate = a_TexCoordinate;
  v_Position.x = a_Position.x;
  v_Position.y = a_Position.y;
  gl_Position =   u_MTransform * a_Position;
}
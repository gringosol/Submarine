precision mediump float;
uniform mat4 u_MTransform;
attribute vec4 a_Position;

void main() {
  gl_Position =   u_MTransform * a_Position;
}
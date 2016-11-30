precision mediump float;
uniform mat4 u_MTransform;
attribute vec4 a_Position;
attribute vec2 a_TexCoordinate;
varying vec2 v_TexCoordinate;

void main() {
  v_TexCoordinate = a_TexCoordinate;
  gl_Position =   u_MTransform * a_Position;
}
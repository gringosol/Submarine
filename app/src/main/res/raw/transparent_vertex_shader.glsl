precision mediump float;
uniform mat4 u_MTransform;
uniform float u_Transp;
attribute vec4 a_Position;
attribute vec2 a_TexCoordinate;
varying vec2 v_TexCoordinate;
varying float v_transp;

void main() {
  v_TexCoordinate = a_TexCoordinate;
  v_transp = u_Transp;
  gl_Position =   u_MTransform * a_Position;
}
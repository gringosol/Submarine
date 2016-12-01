precision mediump float;
uniform mat4 u_MTransform;
uniform vec4 u_Color;
attribute vec4 a_Position;
attribute vec2 a_TexCoordinate;
varying vec2 v_TexCoordinate;
varying vec4 v_Color;

void main() {
  v_TexCoordinate = a_TexCoordinate;
  v_Color = u_Color;
  gl_Position =   u_MTransform * a_Position;
}
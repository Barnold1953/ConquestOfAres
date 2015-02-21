uniform mat4 uMVPMatrix;

attribute vec3 vPosition;
attribute vec4 vColor;
attribute vec2 vTextCoords;

varying vec4 color;
varying vec2 tCoords;

void main() {
  color = vColor;
  tCoords = vTextCoords;
  gl_Position =  unWVP * vPosition;
}

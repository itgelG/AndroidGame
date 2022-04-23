precision mediump float;

varying highp vec2 v_TexCoordinate;
const highp vec2 center = vec2(0.5, 0.5);
const highp float radius = 0.5;
varying vec3 fragColour;

void main() {
	highp float distanceFromCenter = distance(center, v_TexCoordinate);
    lowp float checkForPresenceWithinCircle = step(distanceFromCenter, radius);
	if (checkForPresenceWithinCircle)
		gl_FragColor = vec4(1.0, 1.0, 0.0, 1.0) * checkForPresenceWithinCircle;
	else
		gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
//	gl_FragColor = vec4(fragColour, 1.0);
}
#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform float Dissolve;
uniform vec4 DistortionStrength;
uniform float GameTime;

in float vertexDistance;
in vec4 vertexColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

float rand(vec2 n) {
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float noise(vec2 p){
    vec2 ip = floor(p);
    vec2 u = fract(p);
    u = u*u*(3.0-2.0*u);

    float res = mix(
    mix(rand(ip),rand(ip+vec2(1.0,0.0)),u.x),
    mix(rand(ip+vec2(0.0,1.0)),rand(ip+vec2(1.0,1.0)),u.x),u.y);
    return res*res;
}

void main() {
    vec2 distort = texture(Sampler1, texCoord0 + GameTime/1200 * DistortionStrength.zw).xy * DistortionStrength.xy;
    vec4 col = texture(Sampler0, texCoord0 + distort);
    col = 2 * col;
    col = clamp(col, 0.0, 1.0);
    if (color.a <  1-dissolve){
        discard;
    }
    fragColor = col * linear_fog_fade(vertexDistance, FogStart, FogEnd);
}

#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform float dissolve;
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
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a <  1-dissolve){
        discard;
    }
    color.a *= 1-((noise(texCoord0 * 10 + GameTime * 6000)*0.25f) + 0.25f);
    if (color.a <  1-dissolve){
        discard;
    }
    color *= vertexColor * ColorModulator;
    color *= 1.0 - ((noise(texCoord0 * 10 + GameTime * 6000)/2f) * 0.25);

    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    fragColor = color * linear_fog_fade(vertexDistance, FogStart, FogEnd);
}

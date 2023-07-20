#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec3 normal;
in vec3 pos;
in vec4 baseColor;
in vec4 band;

flat in int layer;
flat in float isBlackHole;

out vec4 fragColor;

//	Simplex 3D Noise 
//	by Ian McEwan, Ashima Arts
//
vec4 permute(vec4 x){return mod(((x*34.0)+1.0)*x, 289.0);}
vec4 taylorInvSqrt(vec4 r){return 1.79284291400159 - 0.85373472095314 * r;}

float snoise(vec3 v){ 
  const vec2  C = vec2(1.0/6.0, 1.0/3.0) ;
  const vec4  D = vec4(0.0, 0.5, 1.0, 2.0);

// First corner
  vec3 i  = floor(v + dot(v, C.yyy) );
  vec3 x0 =   v - i + dot(i, C.xxx) ;

// Other corners
  vec3 g = step(x0.yzx, x0.xyz);
  vec3 l = 1.0 - g;
  vec3 i1 = min( g.xyz, l.zxy );
  vec3 i2 = max( g.xyz, l.zxy );

  //  x0 = x0 - 0. + 0.0 * C 
  vec3 x1 = x0 - i1 + 1.0 * C.xxx;
  vec3 x2 = x0 - i2 + 2.0 * C.xxx;
  vec3 x3 = x0 - 1. + 3.0 * C.xxx;

// Permutations
  i = mod(i, 289.0 ); 
  vec4 p = permute( permute( permute( 
             i.z + vec4(0.0, i1.z, i2.z, 1.0 ))
           + i.y + vec4(0.0, i1.y, i2.y, 1.0 )) 
           + i.x + vec4(0.0, i1.x, i2.x, 1.0 ));

// Gradients
// ( N*N points uniformly over a square, mapped onto an octahedron.)
  float n_ = 1.0/7.0; // N=7
  vec3  ns = n_ * D.wyz - D.xzx;

  vec4 j = p - 49.0 * floor(p * ns.z *ns.z);  //  mod(p,N*N)

  vec4 x_ = floor(j * ns.z);
  vec4 y_ = floor(j - 7.0 * x_ );    // mod(j,N)

  vec4 x = x_ *ns.x + ns.yyyy;
  vec4 y = y_ *ns.x + ns.yyyy;
  vec4 h = 1.0 - abs(x) - abs(y);

  vec4 b0 = vec4( x.xy, y.xy );
  vec4 b1 = vec4( x.zw, y.zw );

  vec4 s0 = floor(b0)*2.0 + 1.0;
  vec4 s1 = floor(b1)*2.0 + 1.0;
  vec4 sh = -step(h, vec4(0.0));

  vec4 a0 = b0.xzyw + s0.xzyw*sh.xxyy ;
  vec4 a1 = b1.xzyw + s1.xzyw*sh.zzww ;

  vec3 p0 = vec3(a0.xy,h.x);
  vec3 p1 = vec3(a0.zw,h.y);
  vec3 p2 = vec3(a1.xy,h.z);
  vec3 p3 = vec3(a1.zw,h.w);

//Normalise gradients
  vec4 norm = taylorInvSqrt(vec4(dot(p0,p0), dot(p1,p1), dot(p2, p2), dot(p3,p3)));
  p0 *= norm.x;
  p1 *= norm.y;
  p2 *= norm.z;
  p3 *= norm.w;

// Mix final noise value
  vec4 m = max(0.6 - vec4(dot(x0,x0), dot(x1,x1), dot(x2,x2), dot(x3,x3)), 0.0);
  m = m * m;
  return 42.0 * dot( m*m, vec4( dot(p0,x0), dot(p1,x1), 
                                dot(p2,x2), dot(p3,x3) ) );
}

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;

    if(isBlackHole > 0.5) {
        color = baseColor;
        vec3 n = normalize(normal);
        if(layer == 1 || layer == 2) n = -n;

        if((layer == 2 || layer == 1) && dot(pos, normal) < 0.0) {
            discard;
        }

        if(layer == 3){
            color.rgb = mix(color.rgb, vec3(0.95, 0.8, 0.9), pow(1.0 - max(-dot(n, normalize(pos)), 0.0), 7.0));
        }

        if(layer == 2) {
            color.rgb = mix(color.rgb, -normal * 0.5 + 0.5, 0.3);
        }

        if(layer == 1) {
            color.rgb = mix(color.rgb, -normal * 0.5 + 0.5, 0.3);

//            vec2 uv = band.zw;
//            uv.y *= 3.1415 * 2.0 / 4.0;
//            uv.x *= 3.1415 * 2.0;
//            float noise = snoise(vec3(uv.y, sin(uv.x + GameTime * 1200.0), cos(uv.x + GameTime * 1200.0))) * 0.6;
//            noise += (snoise(vec3(uv.y, sin(uv.x - GameTime * 1200.0), cos(uv.x - GameTime * 1200.0)) * vec3(4.0)) * 2.0 - 1.0) * 0.2;
//            noise += (snoise(vec3(uv.y, sin(uv.x - GameTime * 800.0), cos(uv.x - GameTime * 800.0)) * vec3(8.0)) * 2.0 - 1.0) * 0.2;
//            noise -= pow(abs(band.w - 0.5) * 2.0, 2.0) * (8.0 + 100.0 * max(snoise(vec3(uv.y, sin(uv.x + GameTime * 800.0), cos(uv.x + GameTime * 800.0)) * vec3(3.0)), 0.0)) - 1.0;
//
//            if(dot(pos, normal) < 0.0) {
//                color.rgb = vec3(0.0);
//                color.a = smoothstep(0.2, 0.3, noise);
//                if(color.a < 0.1) discard;
//            }else{
//                color = mix(color, vec4(0.2, 0.1, 0.3, 1.0), smoothstep(0.2, 0.3, noise));
//            }
        }

        if(layer == 0) {
            color.rgb = vec3(0.0);
            if(dot(pos, normal) > 0.0) color.rgb = vec3(0.2, 0.1, 0.3);

            vec2 uv = band.xy;
            uv.y *= 3.1415 * 2.0 / 4.0;
            uv.x *= 3.1415 * 2.0;
            float noise = snoise(vec3(uv.y, sin(uv.x + GameTime * 1200.0), cos(uv.x + GameTime * 1200.0))) * 0.6;
            noise += (snoise(vec3(uv.y, sin(uv.x - GameTime * 1200.0), cos(uv.x - GameTime * 1200.0)) * vec3(4.0)) * 2.0 - 1.0) * 0.2;
            noise += (snoise(vec3(uv.y, sin(uv.x - GameTime * 800.0), cos(uv.x - GameTime * 800.0)) * vec3(8.0)) * 2.0 - 1.0) * 0.2;
            noise -= pow(abs(band.y - 0.5) * 2.0, 2.0) * (8.0 + 100.0 * max(snoise(vec3(uv.y, sin(uv.x + GameTime * 800.0), cos(uv.x + GameTime * 800.0)) * vec3(3.0)), 0.0)) - 1.0;
            
            color.a = smoothstep(0.2, 0.3, noise);

            float glow = smoothstep(-2.0, 0.3, noise);
            color = mix(vec4(0.4, 0.3, 0.6, 1.0) * vec4(glow), color, color.a);
        }
    }

    if (color.a < 0.1) {
        discard;
    }

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}

#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;
uniform int FogShape;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;
out vec3 normal;
out vec3 pos;
out vec4 baseColor;
out vec4 band;

flat out int layer;
flat out float isBlackHole;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    pos = Position;

    vertexDistance = fog_distance(ModelViewMat, IViewRotMat * Position, FogShape);
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color);
    lightMapColor = texelFetch(Sampler2, UV2 / 16, 0);
    overlayColor = texelFetch(Sampler1, UV1, 0);
    texCoord0 = UV0;
    normal = Normal;
    band = vec4(0.0);

    isBlackHole = 0.0;

    if(Color.rgb == vec3(1.0, 0.0, 0.0) || Color.rgb == vec3(0.0, 1.0, 0.0)) {
        layer = 0;
        isBlackHole = 1.0;
        baseColor = vec4(0.0);

        if(gl_VertexID % 24 == 0)
            band = vec4(0.0, 0.0, 0.0, 0.0);

        if(gl_VertexID % 24 == 1)
            band = vec4(0.0, 1.0, 0.25, 0.0);
            
        if(gl_VertexID % 24 == 2)
            band = vec4(0.25, 1.0, 0.25, 1.0);

        if(gl_VertexID % 24 == 3)
            band = vec4(0.25, 0.0, 0.0, 1.0);

        if(gl_VertexID % 24 == 4)
            band = vec4(0.50, 0.0, 0.75, 1.0);

        if(gl_VertexID % 24 == 5)
            band = vec4(0.50, 1.0, 0.50, 1.0);

        if(gl_VertexID % 24 == 6)
            band = vec4(0.75, 1.0, 0.50, 0.0);

        if(gl_VertexID % 24 == 7)
            band = vec4(0.75, 0.0, 0.75, 0.0);

        if(gl_VertexID % 24 == 8)
            band = vec4(0.0, 0.0, 0.25, 1.0);

        if(gl_VertexID % 24 == 9)
            band = vec4(0.0, 0.0, 0.25, 0.0);

        if(gl_VertexID % 24 == 10)
            band = vec4(0.0, 0.0, 0.50, 0.0);

        if(gl_VertexID % 24 == 11)
            band = vec4(0.0, 0.0, 0.50, 1.0);

        if(gl_VertexID % 24 == 12)
            band = vec4(0.25, 0.0, 0.0, 0.0);

        if(gl_VertexID % 24 == 13)
            band = vec4(0.25, 1.0, 0.0, 0.0);

        if(gl_VertexID % 24 == 14)
            band = vec4(0.50, 1.0, 0.0, 0.0);

        if(gl_VertexID % 24 == 15)
            band = vec4(0.50, 0.0, 0.0, 0.0);

        if(gl_VertexID % 24 == 16)
            band = vec4(0.0, 0.0, 1.0, 0.0);

        if(gl_VertexID % 24 == 17)
            band = vec4(0.0, 0.0, 1.0, 1.0);

        if(gl_VertexID % 24 == 18)
            band = vec4(0.0, 0.0, 0.75, 1.0);

        if(gl_VertexID % 24 == 19)
            band = vec4(0.0, 0.0, 0.75, 0.0);

        if(gl_VertexID % 24 == 20)
            band = vec4(1.0, 1.0, 0.0, 0.0);

        if(gl_VertexID % 24 == 21)
            band = vec4(1.0, 0.0, 0.0, 0.0);

        if(gl_VertexID % 24 == 22)
            band = vec4(0.75, 0.0, 0.0, 0.0);

        if(gl_VertexID % 24 == 23)
            band = vec4(0.75, 1.0, 0.0, 0.0);
    }

    
    if(Color.rgb == vec3(0.0, 1.0, 0.0)) {
        layer = 1;
        isBlackHole = 1.0;
        baseColor = vec4(0.36, 0.19, 0.51, 0.8);
    }

    
    if(Color.rgb == vec3(0.0, 0.0, 1.0)) {
        layer = 2;
        isBlackHole = 1.0;
        baseColor = vec4(0.73, 0.24, 0.89, 1.0);
    }
    
    if(Color.rgb == vec3(1.0, 1.0, 0.0)) {
        layer = 3;
        isBlackHole = 1.0;
        baseColor = vec4(0.0, 0.0, 0.0, 1.0);
    }
}

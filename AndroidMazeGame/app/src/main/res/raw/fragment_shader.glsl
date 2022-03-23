precision mediump float;// Өгөгдмөл нарийвчлалыг дундаж болгоно, Нээх өндөр фрагмент нарийвчлал хэрэггүй

uniform sampler2D u_Texture;// Оролтын бүтэц
uniform vec3 u_LightPos;// Нүдний орон зай дахь гэрлийн байрлал
varying vec3 v_Position;// Энэ фрагментийн интерполяцлагдсан байрлал
varying vec4 v_Color;// Оройн шэйдерээс авсан өнгө
// фрагмент бүрт гурвалжин.
varying vec3 v_Normal;// фрагментийн хувьд хэвийн интерполяци хийсэн
varying vec2 v_TexCoordinate;// Фрагмент бүрт интерполяцлагдсан бүтэцтэй координат

// Манай фрагмент шэйдерийн нэвтрэх цэг
void main() {
    // Сунгах зорилгоор ашиглах болно
    float distance = length(u_LightPos - v_Position);
    // Гэрлээс орой хүртэл гэрэлтүүлгийн чиглэлийн векторыг авна.
    vec3 lightVector = normalize(u_LightPos - v_Position);
    // Хэрэв хэвийн ба гэрлийн вектор нь байвал, гэрлийн вектор ба оройн хэвийн цэгийн үржвэрийг тооцоолох
    // ижил чиглэлд чиглүүлбэл хамгийн их гэрэлтүүлэг авах болно
    float diffuse = max(dot(v_Normal, lightVector), 0.1);
    // Унтралт нэмэх
    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));
    // Орчны гэрэлтүүлэг нэмнэ
    diffuse = diffuse + 0.3;
    // Эцсийн гаралтын өнгийг авахын тулд өнгийг сарнисан гэрэлтүүлгийн түвшингээр үржүүлнэ
    gl_FragColor = diffuse * texture2D(u_Texture, v_TexCoordinate);
}
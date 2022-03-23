uniform mat4 u_MVPMatrix;	// Хосолсон загвар/харагдах/проекцийн матрицыг төлөөлөх тогтмол.

attribute vec4 a_Position; 	// Орой бүрийн байрлалын мэдээллийг бид оруулах болно.
attribute vec2 a_TexCoordinate;   // Орой бүрийн бүтцийн координатын мэдээллийг бид дамжуулах болно.

varying vec2 v_TexCoordinate;   // Үүнийг фрагмент шэйдер рүү шилжүүлэх болно.

void main() {
    // gl_Position нь эцсийн байрлалыг хадгалахад хэрэглэгддэг тусгай хувьсагч юм.
    // Дэлгэцийн хэвийн координатын эцсийн цэгийг авахын тулд оройг матрицаар үржүүлнэ.
    gl_Position = u_MVPMatrix * a_Position;
    // Бүтцийн координатаар дамжина
    v_TexCoordinate = a_TexCoordinate;
}

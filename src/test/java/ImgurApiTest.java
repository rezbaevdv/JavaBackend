import java.io.File;
import java.io.IOException;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


class ImgurApiTest extends BaseApiTest {

    private String currentImageId;
    private String currentCommentId;
    private String currentDeleteHashAlbum;

    public ImgurApiTest() throws IOException {
    }


    @BeforeEach
        void setUp() {
            RestAssured.baseURI = getBaseUri();
        }


        @AfterEach
        void tearDown() {

        }
    @Test

        @Order(1)
        @DisplayName("Получение информации об аккаунте")
        void testGetAccountBase() {

            given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .auth()
                    .oauth2(getToken())
                    .expect()
                    .body("data.url", is("Rezbaevdv"))
                    .log()
                    .all()
                    .statusCode(200)
                    .when()
                    .get("3/account/{username}", getUserName());
        }

        @Order(5)
        @Test
        @DisplayName("Тест загрузки комментария")
        void testCommentUpload() {

            currentCommentId = given()
                    .auth()
                    .oauth2(getToken())
                    .when()
                    .header(new Header("content-type", "multipart/form-data"))
                    .multiPart("image_id", "xj1b0fN")
                    .multiPart("comment", "Hi!3")
                    .expect()
                    .statusCode(200)
                    .body("data.id", is(notNullValue()))
                    .log()
                    .all()
                    .when()
                    .post("3/comment")
                    .jsonPath()
                    .getString("data.id");
        }

        @Order(6)
        @Test
        @DisplayName("Тест удаления комментария")
        void testCommentDelete() {
            String commentId = currentCommentId;

            given()
                    .auth()
                    .oauth2(getToken())
                    .when()
                    .header(new Header("content-type", "multipart/form-data"))
                    .expect()
                    .log()
                    .all()
                    .when()
                    .get("comment/{commentId}", commentId)
                    .jsonPath();
        }
    @Test

        @Order(2)
        @DisplayName("Тест загрузки картинки")
        void testImageUpload() {

            currentImageId = given()
                    .auth()
                    .oauth2(getToken())
                    .when()
                    .header(new Header("content-type", "multipart/form-data"))
                    .multiPart("image", new File("./src/main/resources/res.jpg"))
                    .expect()
                    .statusCode(200)
                    .body("data.id", is(notNullValue()))
                    .body("data.deletehash", is(notNullValue()))
                    .log()
                    .all()
                    .when()
                    .post("3/upload")
                    .jsonPath()
                    .getString("data.id");
        }

        @Order(4)
        @Test
        @DisplayName("Тест картинки Аватара")

        void testAccountImages()  {

            given()
                    .auth()
                    .oauth2(getToken())
                    .when()
                    .header(new Header("content-type", "multipart/form-data"))
                    .expect()
                    .statusCode(200)
                    .body("data.avatar", is(notNullValue()))
                    .log()
                    .all()
                    .when()
                    .get("3/account/{username}/avatar", getUserName())
                    .jsonPath();
        }

        @Order(3)
        @Test
        @DisplayName("Тест создание Альбома")
        void testAlbumCreation() {

            currentDeleteHashAlbum = given()
                    .auth()
                    .oauth2(getToken())
                    .when()
                    .header(new Header("content-type", "multipart/form-data"))
                    .multiPart("ids[]", "xj1b0fN")
                    .multiPart("title", "Album New")
                    .multiPart("description", "This albums contains a lot of dank memes.")
                    .expect()
                    .statusCode(200)
                    .body("data.id", is(notNullValue()))
                    .log()
                    .all()
                    .when()
                    .post("3/album")
                    .jsonPath()
                    .getString("data.deletehash");
        }

        @Order(7)
        @Test
        @DisplayName("Тест удаления Альбома")
        void testAlbumDeletion() {

            given()
                    .auth()
                    .oauth2(getToken())
                    .when()
                    .header(new Header("content-type", "multipart/form-data"))
                    .expect()
                    .log()
                    .all()
                    .when()
                    .get("3/album/{albumHash}", currentDeleteHashAlbum)
                    .jsonPath();
        }
}
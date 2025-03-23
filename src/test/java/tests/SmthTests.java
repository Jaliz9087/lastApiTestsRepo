//package tests;
//
//import io.qameta.allure.Description;
//import io.qameta.allure.Owner;
//import models.RequestCreateToken;
//import models.ResponseCreateToken;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import static io.qameta.allure.Allure.step;
//
//@Owner("-whey-")
//public class SmthTests {
//    private static final Logger log = LoggerFactory.getLogger(SmthTests.class);
//    @Test
//    @DisplayName("Create User Test")
//    @Description("Post")
//            void createTokenTest() {
//        RequestCreateToken ReqCT = new RequestCreateToken();
//        ReqCT.setUsername("admin");
//        ReqCT.setPassword("password123");
//        ResponseCreateToken RespCT = step ("our req", () -> {
//            given Specifications.requestSpec(ReqCT)
//                    .when()
//                    .post("/v1/user/login")
//                    .then()
//                    .spec(Specifications.responseSpec(200))
//                    .extract().as(ResponseCreateToken.class);
//        }
//    }
//}

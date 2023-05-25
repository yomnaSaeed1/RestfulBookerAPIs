package Bookings;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;


public class AuthenticationAndbooking {

    //Body for create token
    String credentials = "{\n" +
            "    \"username\" : \"admin\",\n" +
            "    \"password\" : \"password123\"\n" +
            "}";

    public int id;

    //Body for create booking
    String body = "{\n" +
            "    \"firstname\" : \"Jim\",\n" +
            "    \"lastname\" : \"Brown\",\n" +
            "    \"totalprice\" : 111,\n" +
            "    \"depositpaid\" : true,\n" +
            "    \"bookingdates\" : {\n" +
            "        \"checkin\" : \"2018-01-01\",\n" +
            "        \"checkout\" : \"2019-01-01\"\n" +
            "    },\n" +
            "    \"additionalneeds\" : \"Breakfast\"\n" +
            "}";

    //Body for update booking
    String updatedBody = "{\n" +
            "    \"firstname\" : \"James\",\n" +
            "    \"lastname\" : \"Brown\",\n" +
            "    \"totalprice\" : 111,\n" +
            "    \"depositpaid\" : true,\n" +
            "    \"bookingdates\" : {\n" +
            "        \"checkin\" : \"2018-01-01\",\n" +
            "        \"checkout\" : \"2019-01-01\"\n" +
            "    },\n" +
            "    \"additionalneeds\" : \"Breakfast\"\n" +
            "}";

    //Body for partial update
    String partialUpdatedBody = "{\n" +
            "    \"firstname\" : \"James\",\n" +
            "    \"lastname\" : \"Brown\"\n" +
            "}";



    //Create Token Method
    @Test(priority = 0)
    public void createToken(){
        RestAssured
                .given().baseUri("https://restful-booker.herokuapp.com")
                .contentType(ContentType.JSON)
                .body(credentials)
                .log().all()
                .when().post("/auth")
                .then().log().all().assertThat().statusCode(200);
    }

    //Create Booking
    @Test(priority = 1)
    public void createBookingHasID(){
        Response res =
                given().baseUri("https://restful-booker.herokuapp.com")
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(body)
                        .when().post("/booking")
                        .then().assertThat().body("", hasKey("bookingid")).extract().response();

        System.out.println(res.asString());
         id = res.path("bookingid");

        System.out.println(id);
    }


    //update booking
    @Test(priority = 2)
    public void updateBooking(){
        given().baseUri("https://restful-booker.herokuapp.com")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .body(updatedBody)
                .pathParam("id", id)
                .when().put("/booking/{id}")
                .then().log().all().assertThat().body("firstname", equalTo("James"));
    }

    //Partial update for the booking
    @Test(priority = 3)
    public void partialUpdate(){
        given().baseUri("https://restful-booker.herokuapp.com")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .body(partialUpdatedBody)
                .pathParam("id", id)
                .when().patch("/booking/{id}")
                .then().log().all()
                .assertThat().statusCode(200)
                .body("firstname", equalTo("James"))
                .body("lastname", equalTo("Brown"));
    }


    //Delete booking
    @Test(priority = 4)
    public void deleteBooking(){
        given().baseUri("https://restful-booker.herokuapp.com")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .pathParam("id", id)
                .when().delete("/booking/{id}")
                .then().log().all().assertThat().statusCode(201);
    }
}

package hello;

//import hello.entities.FixRepository;
//import hello.entities.Fix;
import javafx.scene.canvas.GraphicsContext;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.Driver;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;


import static org.apache.http.protocol.HTTP.USER_AGENT;
import static org.neo4j.driver.v1.Values.parameters;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.sql.*;
import java.util.Properties;


@RestController
public class Controller {


//    @GetMapping("/nongreeting")
//    public String nongreet() {
//
//
//        System.out.println("ENTERED3");
//        return "nongreeting";
//    }

//    }

    public static String fixIssues(String modifiers)
    {

        String ans = "[";
        if(modifiers.contains("public"))
        {
            ans += "public, ";

        }
        if(modifiers.contains("protected"))
        {
            ans += "protected, ";
        }
        if(modifiers.contains("private"))
        {
            ans += "private, ";
        }
        if(modifiers.contains("abstract"))
        {
            ans += "abstract, ";
        }
        if(modifiers.contains("static"))
        {
            ans += "static, ";
        }
        if(modifiers.contains("final"))
        {
            ans += "final, ";
        }
        if(modifiers.contains("transient"))
        {
            ans += "transient, ";
        }
        if(modifiers.contains("volatile"))
        {
            ans += "volatile, ";
        }
        if(modifiers.contains("synchronized"))
        {
            ans += "synchronized, ";
        }
        if(modifiers.contains("native"))
        {
            ans += "native, ";
        }
        if(modifiers.contains("strictfp"))
        {
            ans += "strictfp, ";
        }


        if(ans.endsWith(", "))
        {
            ans = ans.substring(0, ans.length()-2);
            ans += "]";
        }
        return ans;

    }
    //POST generator for codegen
    public static String sendPOST(String file_id_array) throws Exception {

        String body = new String("{\"url\" : \"bolt://codegraph-staging-sandbox.devfactory.com:20791\",\"username\" : \"neo4j\",\"password\" : \"password\",\"fileIds\" :" + file_id_array + "}");

        System.out.println("heresp");

        JSONObject jsonObj = new JSONObject(body);
        System.out.println("heresp2");

        String url = "http://codegen-cnu.ey.devfactory.com/api/codegen/";
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        post.setHeader("Accept", "application/json");
        post.setHeader("headerValue", "HeaderInformation");
//setting json object to post request.
        System.out.println(jsonObj.toString());
        StringEntity entity = new StringEntity(jsonObj.toString(), "UTF8");
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        post.setEntity(entity);
//this is your response:
        System.out.println("ABOUT TO DO SOMETHING");
        HttpResponse response = client.execute(post);

        System.out.println(response);


        HttpEntity responseEntity = response.getEntity();
        String responseString = EntityUtils.toString(responseEntity, "UTF-8");
        System.out.println(responseString);
        return responseString;
    }

    //GET generator for codegen
    private static String sendGET(Integer id) throws IOException {
        URL obj = new URL("http://codegen-cnu.ey.devfactory.com/api/codegen/status/" + String.valueOf(id));
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        StringBuffer response = new StringBuffer();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
        }

        return response.toString();

    }



    /*******************GET REQUEST*************/

    @RequestMapping(value = "/fix/{fixID}", method=RequestMethod.GET)
    public String fixGet(@PathVariable("fixID") Long fixID) {

        Integer issueType = 2;
        String ret = new String();
         try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/fixes", "springuser", "ThePassword")) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from fix WHERE fixid = " + String.valueOf(fixID) + ";");

            while(rs.next())
            {
                issueType = rs.getInt(3);
                ret += "FixId: " + rs.getInt(1) + "  " + "Status:" + rs.getString(2) + "  " + "IssueType:" + rs.getInt(3)+  "  " + "s3Link:" + rs.getString(4) + "\r\n" + System.lineSeparator();
            }

            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        String ret2 = new String();
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/fixes", "springuser", "ThePassword")) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from issues WHERE issueType = " + String.valueOf(issueType) + ";");

            while(rs.next())
            {
                ret2 += "id: " + rs.getInt(1) + "  " + "issueType:" + rs.getString(2) + "  " + "line:" + rs.getInt(3)+  "  " + "file:" + rs.getString(4) + "  " + "col:" + rs.getString(5) + " " + "fixed:" + rs.getString(6) + "\r\n" + System.lineSeparator();
            }

            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        return ret2.toString();
    }


    /*******************POST REQUEST*************/

    @RequestMapping(value="/fix",method=RequestMethod.POST)
    @ResponseBody
    public String fixPost(@ModelAttribute("issueType") Integer issueType,
                          @ModelAttribute("sandBoxUrl") String sandBoxUrl) throws Exception {
        String return_fixid = "na", return_status = "na";

        try (Connection con = DriverManager.getConnection("jdbc:neo4j:" + sandBoxUrl, "neo4j", "password") ) {

            // Querying
            String query =

                    "MATCH (u)<-[:tree_edge*]-(f:File) WHERE EXISTS(u.modifiers) AND NOT u.modifiers = \"[]\" AND u.modifiers =~ \"\\\\[(public(, )?|private(, )?|protected(, )?)?(static(, )?)?(abstract(, )?)?(synchronized(, )?)?(transient(, )?)?(volatile(, )?)?(default(, )?)?(final(, )?)?(native(, )?)?(strictfp(, )?)?\\\\]\" RETURN u.modifiers, ID(u), u.line, u.file, u.col, ID(f) LIMIT 60";
            String file_id_concat = "";
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {

                        String node_id = rs.getString("ID(u)");
                        String file_id = rs.getString("ID(f)");
                        String line = rs.getString("u.line");
                        String file = rs.getString("u.file");
                        String col = rs.getString("u.col");
                        String modifiers = rs.getString("u.modifiers");

                        if(!file_id_concat.contains(file_id))
                            file_id_concat += file_id + ", ";
                        System.out.println(file_id_concat);

                        //id | issueType | line | file | col  | fixed
                        try (Connection conSQL = DriverManager.getConnection("jdbc:mysql://localhost:3306/fixes", "springuser", "ThePassword")) {
                            Statement SQLquery = conSQL.createStatement();
                            SQLquery.executeUpdate("insert into issues (id, issueType, line, file, col, fixed) VALUES (" +
                                    node_id + ", " + issueType + ", " + line + ", \"" + file + "\", " + col + ", " + "\"Unresolved\"" + ");");

                            //


                            System.out.println(modifiers);
                            String newModifiers = fixIssues(modifiers); //changes status for each
                            System.out.println(newModifiers);


                            String modifyQuery = "MATCH (u)  WHERE ID(u) = " + node_id + " SET u.modifiers =\"" + newModifiers + "\" RETURN u.modifiers as str";
                            ResultSet modifiedNode = con.prepareStatement(modifyQuery).executeQuery();


                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }

                            String file_id_array = "[" + file_id_concat;
                            file_id_array= file_id_array.substring(0, file_id_array.length()-2);
                            file_id_array += "]";

                            System.out.println(file_id_array);


                            String response = sendPOST(file_id_array);
                            JSONObject json = new JSONObject(response);
                            Integer id = json.getInt("id");
                            System.out.println(id);


                            String s3Link, status;
                            while(true) {
                                String response2 = sendGET(id);


                                JSONObject json2 = new JSONObject(response2);
                                status = json2.getString("status");
                                s3Link = "N/A";
                                if (status.equals("Success")) {
                                    s3Link = json2.getString("Url");
                                    break;
                                }

                            }

                            //update s3 link in database
                            try (Connection newcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/fixes", "springuser", "ThePassword")) {

                                String createFix = "insert into fix(fixid, status, issuetype, s3Link) VALUES (" + id + ", \"" + status + "\", " + String.valueOf(issueType) + ", " + "\"" + s3Link + "\")";
                                newcon.prepareStatement(createFix).executeUpdate(createFix);

                            }catch (Exception e) {
                                System.out.println(e);
                            }

                            return_fixid = String.valueOf(id);
                            return_status = status;


                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        //System.out.println("plzgod");
        return "Fixid = " + return_fixid + "Status = " + return_status;
    }
}





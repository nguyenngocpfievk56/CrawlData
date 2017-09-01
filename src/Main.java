import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by nguyenhuungoc on 2017/07/10.
 */
public class Main {

    final static String BASE_URL = "http://www.yamada-denkiweb.com";

    public static void main(String[] args) {
        MySQLAccess mySQLAccess = new MySQLAccess();
        String urlMySQL = "jdbc:mysql://localhost:3306/match?useUnicode=true&characterEncoding=utf8";
        String username = "root";
        String password = "root";
        mySQLAccess.connect(urlMySQL, username, password);

        ArrayList<Category> catsList= new ArrayList<>();

        try {
            Document doc = Jsoup.connect(BASE_URL).get();
            Element catsDiv = doc.getElementById("cats");
            Elements cats = catsDiv.getElementsByTag("a");

            for (int i=0; i<cats.size(); i++){
                String href = cats.get(i).attr("href");
                String name = Jsoup.parse(cats.get(i).html()).text();

                Category cat = new Category(i, name, href);
                catsList.add(cat);

                //Save all categories here
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        for (int i=0; i<catsList.size(); i++) {
            Category cat = catsList.get(i);

            try {
                Document doc = Jsoup.connect(BASE_URL + cat.url).get();
                Elements pagis = doc.getElementsByClass("pagination pagination-right");
                String pageLink = pagis.get(0).getElementsByTag("a").get(2).attr("href");
                pageLink = pageLink.substring(0, pageLink.length() - 2);

                getProductsInPage(cat.id, BASE_URL + cat.url + pageLink, mySQLAccess);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void getProductsInPage(int idCat, String url, MySQLAccess mySQLAccess) {
        int counter = 0;
        while (counter < 100) {
            Document doc = null;
            try {
                doc = Jsoup.connect(url + counter).get();

                Element productTable = doc.getElementsByClass("item-list-vertical").get(0);
                Element ul = productTable.child(0);
                Elements li = ul.children();
                for (int i=0; i<li.size(); i++) {
                    String surl = li.get(i).child(0).child(0).child(0).attr("href");
                    System.out.println(surl);

                    Document doc1 = Jsoup.connect(BASE_URL + surl).get();
                    Element detailBlock = doc1.getElementsByClass("item-detail-block").first();
                    Element img = detailBlock.getElementsByClass("item-images-images").get(0);
                    String imgUrl = img.getElementsByTag("img").get(0).attr("src");
                    String name = "";
                    try{
                        name = doc1.getElementsByClass("item-name").get(0).child(0).html();
                    } catch (Exception e) {
                        name = doc1.getElementsByClass("item-name").get(0).html();
                    }

                    Element descriptionCode = doc1.getElementsByClass("item-guide").get(0);
                    Elements aTag = descriptionCode.getElementsByTag("a");
                    for (int j=0; j<aTag.size(); j++){
                        aTag.get(j).remove();
                    }
                    String description = descriptionCode.html();

                    mySQLAccess.insertData("product", new String[]{"name", "description", "img", "idCat"},
                            new String[]{name, description, imgUrl, "" + idCat});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            counter += 20;
        }
    }
}

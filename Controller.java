package sample;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class Controller {
    @FXML
    public void initialize() {
        loadAllArt();
    }

    private int selectedHitlijst = 1;

    @FXML private DatePicker dateTop;
    @FXML private TextField txtZoekArtiest;
    @FXML private TableView<artist> tv;
    @FXML private TableView<single> tv2;
    @FXML private TableView<single> tv3;
    @FXML private TextField txtZoekSingles;

    @FXML
    void zoekArtiest(ActionEvent event) {
        String zoektxt = txtZoekArtiest.getText().trim();
        if (!zoektxt.isEmpty()) {
            showTV1();
            DB.open();
            doQuery("SELECT * FROM artiest WHERE naam LIKE '%" + zoektxt + "%' ", 1);
            //add tablecolumn en vul met resultaten van array data
            TableColumn colArtist = new TableColumn("Artiest");
            tv.getColumns().clear();
            tv.getColumns().add(colArtist);
            tv.setItems(data);
            colArtist.setCellValueFactory(new PropertyValueFactory<artist, String>("name"));
        } else {
            infoBox("Vul aub wat in", "info");
        }
    }
    @FXML
    void allArtist(ActionEvent event) {
        loadAllArt();
    }

    void loadAllArt(){
        showTV1();
        DB.open();
        doQuery("SELECT * FROM artiest", 1);
        //add tablecolumn en vul met resultaten van array data
        TableColumn colArtist = new TableColumn("Artiest");
        tv.getColumns().clear();
        tv.getColumns().add(colArtist);
        tv.setItems(data);
        colArtist.setCellValueFactory(new PropertyValueFactory<artist, String>("name"));
    }

    @FXML
    void zoekSingles(ActionEvent event) {
        String zoektxt = txtZoekSingles.getText().trim();
        if (!zoektxt.isEmpty()) {
            showTV2();
            DB.open();
            doQuery("SELECT * FROM single WHERE titel LIKE '%" + zoektxt + "%' ", 2);
            //maak columns
            TableColumn colSingleTitle = new TableColumn("Single");
            TableColumn colSingleArtist = new TableColumn("Artiest");
            tv2.getColumns().clear();
            tv2.getColumns().addAll(colSingleTitle, colSingleArtist);
            //fill table
            tv2.setItems(data2);
            colSingleTitle.setCellValueFactory(new PropertyValueFactory<single, String>("name"));
            colSingleArtist.setCellValueFactory(new PropertyValueFactory<single, String>("artistName")); //todo : hier gaat iets missss
        } else {
            infoBox("Vul aub wat in", "info");
        }
    }
    @FXML
    void allSingles(ActionEvent event) {
        showTV2();
        DB.open();
        doQuery("SELECT * FROM single", 2);
        //maak columns
        TableColumn colSingleTitle = new TableColumn("Single");
        TableColumn colSingleArtist = new TableColumn("Artiest");
        tv2.getColumns().clear();
        tv2.getColumns().addAll(colSingleTitle, colSingleArtist);
        //fill table
        tv2.setItems(data2);
        colSingleTitle.setCellValueFactory(new PropertyValueFactory<single, String>("name"));
        colSingleArtist.setCellValueFactory(new PropertyValueFactory<single, String>("artistName"));
    }

    //
    @FXML
    void zoekTop40(ActionEvent event) {
        showTV3();
        topper40id.clear();
        LocalDate zoekDatum = dateTop.getValue();
        Locale locale = Locale.US;
        int weekNR = zoekDatum.get(WeekFields.of(locale).weekOfWeekBasedYear());
        int year = zoekDatum.getYear();

        DB.open();
        String listID = doQuery("SELECT id FROM `hitlijst_editie` WHERE `week`="+weekNR+" && `jaar`="+year+" && `hitlijst`="+selectedHitlijst, 6); //+" && `hitlijst`=1"

        if (listID != null){
            DB.open();
            doQuery("SELECT * FROM `hitlijst_notering` WHERE `hitlijst_editie`="+listID,3); //run query & add signleid en positie to data3

            for (int x = 0; x < topper40id.size(); x++){
                int singleID = Integer.parseInt(topper40id.get(x).toString());
                int pos = x+1;
                //System.out.println("trackid "+topper40id.get(x));
                //System.out.println("pos: "+pos);
                DB.open();
                String result = doQuery("SELECT * FROM single WHERE id =" + singleID, 5);
                System.out.println(result);
                String[] datas = result.split("__");
                data2.add(new single(datas[0],Integer.parseInt(datas[1]),Integer.parseInt(datas[2]),datas[3],singleID,pos));
            }
        }
        else {
            //geen gegevens voor deze datum
        }
        TableColumn colTopSingle = new TableColumn("Single");
        TableColumn colTopArtist = new TableColumn("Artiest");
        TableColumn colTopNr = new TableColumn("#");
        colTopNr.setMaxWidth(35.0);
        colTopNr.setMinWidth(35.0);
        tv3.getColumns().clear();
        tv3.getColumns().addAll(colTopNr, colTopSingle, colTopArtist);
        tv3.setItems(data2);
        colTopNr.setCellValueFactory(new PropertyValueFactory<single, String>("positie"));
        colTopSingle.setCellValueFactory(new PropertyValueFactory<single, String>("name"));
        colTopArtist.setCellValueFactory(new PropertyValueFactory<single, String>("artistName"));
    }

    @FXML
    void wisselen(){ //wissel van hitlijst
        if (selectedHitlijst == 1){
            selectedHitlijst = 2;
            infoBox("geselecteerd: tipparade","info");
        }
        else {
            selectedHitlijst = 1;
            infoBox("geselecteerd: top40","info");

        }
    }

    @FXML
    void clickArtist() {
        artist artist = tv.getSelectionModel().getSelectedItem();
        int selectedArtist = artist.getId();
        String selectedArtistName = artist.getName();
        System.out.println(selectedArtist);

        DB.open();
        doQuery("SELECT * FROM single WHERE artiest="+selectedArtist, 2);

        String tracks = "";
        for (int x=0; x < data2.size(); x++){
            System.out.println( data2.get(x).getName() );
            tracks += data2.get(x).getName()+"<br>";
        }

        infoBox("<html>Artiest: "+selectedArtistName+"</b><br><br>"+tracks+"</html>","artiest informatie");

    }
    @FXML
    void clickSingle() {
        int jaar = 10000;
        int week = 0;
        arrSingles.clear();
        arrSingleData.clear();
        single single = tv2.getSelectionModel().getSelectedItem();
        if (single != null) {
            int selected = single.getId();
            DB.open();
            doQuery("SELECT * FROM single WHERE id =" + selected, 2);
            DB.open();
            doQuery("SELECT * FROM `hitlijst_notering` WHERE `single`=" + selected, 8);
            DB.open();
            String res = doQuery("SELECT * FROM single WHERE id =" + selected, 5);
            String[] info = res.split("__");
            int hoogstPos = 40;
            for (int i = 0; i < arrSingles.size(); i++) {
                if (arrSingles.get(i).getPos() < hoogstPos) {
                    hoogstPos = arrSingles.get(i).getPos();
                }
                DB.open();
                doQuery("SELECT `week`,`jaar`,`hitlijst` FROM `hitlijst_editie` WHERE `id`=" + arrSingles.get(i).getEdit(), 9);
                for (int u = 0; u < arrSingleData.size(); u++) {
                    if (arrSingleData.get(u).getJaar() < jaar) {
                        jaar = arrSingleData.get(u).getJaar(); //pak laagste jaar
                    }
                    if ((u + 1) < arrSingleData.size()) {
                        if (arrSingleData.get(u).getWeek() != arrSingleData.get(u + 1).getWeek()) {
                            week++;
                        }
                    }
                }

            }

//        System.out.println(info[1]); //Trackid
//        System.out.println(info[2]); //artistID
            String aTitle = "Titel: " + info[0];
            String aArtiest = "Artiest: " + info[3];
            String aJaar = "Jaartal: " + jaar;
            String aTop = "Weken in top 40: " + week;
            String aPos = "Hoogste positie: " + hoogstPos;

            infoBox("<html>" + aTitle + "<br><br>" + aArtiest + "<br><br>" + aJaar + "<br><br>" + aTop + "<br><br>" + aPos + "</html>", "single informatie");
        }
    }

    //msgbox
    private static void infoBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    //Show correct tableview
    private void showTV1() {
        tv.setVisible(true);
        tv2.setVisible(false);
        tv3.setVisible(false);
    }
    private void showTV2() {
        tv.setVisible(false);
        tv2.setVisible(true);
        tv3.setVisible(false);
    }
    private void showTV3() {
        tv.setVisible(false);
        tv2.setVisible(false);
        tv3.setVisible(true);
    }

    //private String getSingleNaam(int id){
    //    DB.open();
    //    String singlenaam =doQuery("SELECT `titel` FROM `single` WHERE `id`="+id,10);
    //    return singlenaam;
    //}


    //artistnames array
    public static  class artName {
        private final SimpleStringProperty name;
        private final SimpleIntegerProperty id;

        private artName(String name, int id) {
            this.name = new SimpleStringProperty(name);
            this.id = new SimpleIntegerProperty(id);
        }
        public String getName() {
            return name.get();
        }
        public int getId() {
            return id.get();
        }
    }
    static ArrayList<artName> arr = new ArrayList<>();

    public static  class singO {
        private final SimpleIntegerProperty pos;
        private final SimpleIntegerProperty editie;

        private singO(int pos, int editie) {
            this.pos = new SimpleIntegerProperty(pos);
            this.editie = new SimpleIntegerProperty(editie);
        }
        public int getPos() { return pos.get(); }
        public int getEdit() {
            return editie.get();
        }
    }
    static ArrayList<singO> arrSingles = new ArrayList<>();

    public static  class singX {
        private final SimpleIntegerProperty jaar;
        private final SimpleIntegerProperty week;
        private final SimpleIntegerProperty lijst;

        private singX(int jaar, int week, int lijst) {
            this.jaar = new SimpleIntegerProperty(jaar);
            this.week = new SimpleIntegerProperty(week);
            this.lijst = new SimpleIntegerProperty(lijst);
        }
        public int getWeek() { return week.get(); }
        public int getJaar() { return jaar.get(); }
        public int getLijst() { return lijst.get(); }

    }
    static ArrayList<singX> arrSingleData = new ArrayList<>();

    //artists & singels
    public static class artist {
        private final SimpleStringProperty name;
        private final SimpleIntegerProperty id;

        private artist(String fName, int id) {
            this.name = new SimpleStringProperty(fName);
            this.id = new SimpleIntegerProperty(id);
        }

        public String getName() {
            return name.get();
        }
        public int getId() {
            return id.get();
        }
    }

    public static class single {
        private final SimpleIntegerProperty positie;
        private final SimpleIntegerProperty single;
        private final SimpleStringProperty name;
        private final SimpleIntegerProperty id;
        private final SimpleIntegerProperty artistId;
        private final SimpleStringProperty artistName;

        private single(String fName, int fId, int fArtistId, String fArtistName, int single, int positie) {
            this.single = new SimpleIntegerProperty(single);
            this.positie   = new SimpleIntegerProperty(positie);
            this.name = new SimpleStringProperty(fName);
            this.id = new SimpleIntegerProperty(fId);
            this.artistId = new SimpleIntegerProperty(fArtistId);
            this.artistName = new SimpleStringProperty(fArtistName);
        }

        public String getName() {
            return name.get();
        }
        public String getArtistName() {return artistName.get();}
        public int getId() {
            return id.get();
        }
        public int getPositie() {return positie.get();}
    }

    //tabledata lists
    private static ObservableList<artist> data;
    private static ObservableList<single> data2;
    private static List topper40id = new ArrayList();

    //Querys
    public static String doQuery(String query, int type) { //type 1=artist, 2=single, 3=top40
        Statement stmt = null;
        ResultSet rs = null;

        if (type == 1) {data = FXCollections.observableArrayList();}
        //if (type == 3) {data2 = FXCollections.observableArrayList();}
        if (type == 2 || type == 6) {data2 = FXCollections.observableArrayList();}

        //QUERY UITVOEREN
        try {
            stmt = DB.conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) { //loop alle results
                if (type == 1) {
                    //artist query
                    String name = rs.getString("naam");
                    int id = rs.getInt("id");
                    data.add(new artist(name, id));
                }
                if (type == 2 || type == 5) {
                    //singles query
                    String name = rs.getString("titel");
                    int id = rs.getInt("id");
                    int artistId = rs.getInt("artiest");
                    String artNamer="not set";
                    //get artistname van id
                    for(artName fName : arr) {
                        if(fName.getId()==artistId) {
                            //System.out.println( fName.getName() );
                            artNamer = fName.getName();
                        }
                    }
                    if (type == 2) {
                        data2.add(new single(name, id, artistId, artNamer,0,0));
                    }
                    else {
                        String rtrn = name+"__"+id+"__"+artistId+"__"+artNamer;
                        return rtrn;
                    }
                }
                if (type == 3) {
                    //top40 query
                    int positie = rs.getInt("positie");
                    int singleId = rs.getInt("single");
                    topper40id.add(singleId);
                }
                if (type == 4) {
                    //get all artistnames as array
                    String artName = rs.getString("naam");
                    int artID = rs.getInt("id");
                    //System.out.println("naam: "+ artName + " - id: "+ artID);
                    arr.add(new artName(artName,artID));
                }
                if (type == 6) {
                    //get hitlijst id
                    int x =  rs.getInt("id");
                    return x + "";
                }
                if (type == 8) {
                    //get pos & hitlijst editie
                    int pos = rs.getInt("positie");
                    int hitEditie = rs.getInt("hitlijst_editie");
                    arrSingles.add(new singO(pos,hitEditie));
                }
                if (type == 9 ){
                    //get selected year and week
                    int jaar = rs.getInt("jaar");
                    int week = rs.getInt("week");
                    int hitlist = rs.getInt("hitlijst");
                    arrSingleData.add(new singX(jaar,week,hitlist));
                }
                if (type == 10){
                    return (rs.getString("titel"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            infoBox("Fout bij uitvoeren query", "error");
            throw new Error("Fout bij uitvoeren query.");
        }
        //close connection
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                } //ignore
                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                } //ignore
                stmt = null;
            }
            DB.close();
        }
        return null; //bij type 1,2,3 geen return want wordt al in obs.list gezet
    }
}


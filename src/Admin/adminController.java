package Admin;

import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class adminController implements Initializable{
    private dbConnection db;
    private ObservableList<studentData> data;

    @FXML
    private TableView<studentData> studentTable;
    @FXML
    private TableColumn<studentData, String> idcolum;
    @FXML
    private TableColumn<studentData, String> firstnamecolum;
    @FXML
    private TableColumn<studentData, String> lastnamecolum;
    @FXML
    private TableColumn<studentData, String> emailcolum;
    @FXML
    private TableColumn<studentData, String> dobcolum;
    @FXML
    private TextField searchTxt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.db = new dbConnection();
    }
    //load data from Database
    @FXML
    private void loadStudentData(ActionEvent event){
        try {
            Connection conn = dbConnection.getConnection();
            this.data = FXCollections.observableArrayList();
            String sql = "select * from student";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                this.data.add(new studentData(rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5)));
            }//while
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //put student data to tableview
        this.idcolum.setCellValueFactory(
                new PropertyValueFactory<studentData,String>("ID"));
        this.firstnamecolum.setCellValueFactory(
                new PropertyValueFactory<studentData,String>("firstName"));
        this.lastnamecolum.setCellValueFactory(
                new PropertyValueFactory<studentData,String>("lastName"));
        this.emailcolum.setCellValueFactory(
                new PropertyValueFactory<studentData,String>("email"));
        this.dobcolum.setCellValueFactory(
                new PropertyValueFactory<studentData, String>("DOB"));

        this.studentTable.setItems(null);
        this.studentTable.setItems(data);

        //Filter Data in TableView
        FilteredList<studentData> filteredData =
                new FilteredList<>(data, e -> true);
        searchTxt.setOnKeyReleased(e -> {
            searchTxt.textProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        filteredData.setPredicate(studentData -> {
                            if (newValue == null || newValue.isEmpty()) {
                                return true;
                            }
                            String lowerCaseFilter = newValue.toLowerCase();
                            if (studentData.getID().contains(newValue)) {
                                return true;
                            } else if
                                    (studentData.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                                return true;
                            } else if
                                    (studentData.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                                return true;
                            }
                            return false;
                        });
                    });
            SortedList<studentData> sortedData =
                    new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(
                    studentTable.comparatorProperty());
            studentTable.setItems(sortedData);

        });



    }//loadStudentData
    //clear form

    @FXML
    private  void clearFields(ActionEvent event){
        this.id.setText("");
        this.fname.setText("");
        this.lname.setText("");
        this.email.setText("");
        this.dob.setValue(null);
    }


    //add Data
    @FXML
    private void addStudent(ActionEvent event) {
        String sqlInsert = "insert into user(id,firstName,lastName,email,DOB) values (?,?,?,?,?)";

        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlInsert);
            stmt.setString(1, this.id.getText());
            stmt.setString(2, this.fname.getText());
            stmt.setString(3, this.lname.getText());
            stmt.setString(4, this.email.getText());
            stmt.setString(5, this.dob.getEditor().getText());

            stmt.execute();
            stmt.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadStudentData(new ActionEvent());
    }

}//class
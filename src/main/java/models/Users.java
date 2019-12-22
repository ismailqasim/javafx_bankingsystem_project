package models;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Users {
	Firestore db;

	public Users(){
		
		Stage stage = new Stage();
		stage.setTitle("Users");
		VBox vBox = new VBox();
		TextField search = new TextField();
		Button btnSearch = new Button("Search");
		btnSearch.setPrefWidth(100);
		
		HBox searchBar = new HBox(10, search, btnSearch);
		searchBar.setPadding(new Insets(10, 10, 0, 10));
 
		TableView<Account> tableView = new TableView<Account>();
		
		TableColumn<Account, String> column1 = 
				new TableColumn<Account, String>("First Name");
        
        TableColumn<Account, String> column2 = 
				new TableColumn<Account, String>("Last Name");
        
        TableColumn<Account, String> column3 = 
				new TableColumn<Account, String>("Account ID");
        
        TableColumn<Account, String> column4 = 
				new TableColumn<Account, String>("Balance");
        
        TableColumn<Account, String> column5 = 
				new TableColumn<Account, String>("Type");

        column1.setCellValueFactory(new 
        		PropertyValueFactory<Account, String>("firstName"));
        column2.setCellValueFactory(new 
        		PropertyValueFactory<Account, String>("lastName"));
        column3.setCellValueFactory(new 
        		PropertyValueFactory<Account, String>("accountId"));
        column4.setCellValueFactory(new 
        		PropertyValueFactory<Account, String>("balance"));
        column5.setCellValueFactory(new 
        		PropertyValueFactory<Account, String>("accountType"));
        
        tableView.getColumns().addAll(column1, column2, column3, column4, column5);
        
		Scene scene = new Scene(vBox);
		vBox.getChildren().addAll(searchBar, tableView);
		
		stage.setScene(scene);
		stage.show();
		db = FirestoreClient.getFirestore();
		
		loadUsers();
	}
	
	void loadUsers() {
		CollectionReference users = db.collection("users");
		final ApiFuture<QuerySnapshot> future = users.get();
		future.addListener(new Runnable() {
			@Override
			public void run() {
				System.out.println("Data has been loaded!");
			}
		}, new Executor() {
			@Override
			public void execute(Runnable arg0) {
				List<QueryDocumentSnapshot> documents;
				try {
					documents = future.get().getDocuments();
					for (DocumentSnapshot document : documents) {
					  System.out.println(document.toString());
					}
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	void searchUsers(String name) {
//		CollectionReference users = db.collection("users");
//		Query query = users.whereEqualTo("firstName", name);
//		final ApiFuture<QuerySnapshot> future = query.get();
//		future.addListener(new Runnable() {
//			@Override
//			public void run() {}
//		}, new Executor() {
//			@Override
//			public void execute(Runnable arg0) {
//				List<QueryDocumentSnapshot> documents = future.get().getDocuments();
//				for (DocumentSnapshot document : documents) {
//				  
//				}
//			}
//		});
	}
	
}






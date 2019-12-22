import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentChange;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firestore.v1.Document;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Account;

public class Users {
	Firestore db;
	TableView<Account> tableView;

	public Users(){

		Stage stage = new Stage();
		stage.setTitle("Users");
		VBox vBox = new VBox(10);
		final TextField search = new TextField();
		Button btnSearch = new Button("Search");
		btnSearch.setPrefWidth(100);

		HBox searchBar = new HBox(10, search, btnSearch);
		searchBar.setPadding(new Insets(10, 10, 0, 10));

		tableView = new TableView<Account>();

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
		tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		Scene scene = new Scene(vBox);
		vBox.getChildren().addAll(searchBar, tableView);

		stage.setScene(scene);
		stage.show();
		db = FirestoreClient.getFirestore();

		listenData();

		btnSearch.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (search.getText().trim().isEmpty()) {
					loadUsers();
					return;
				}
				searchUsers(search.getText());
			}
		});
		
		tableView.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					new UserDetails(
							tableView.getSelectionModel().getSelectedItem());
				}
			}
			
		});
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
						Account u = new Account();
						u.accountId = document.getLong("accountId");
						u.firstName = document.getString("firstName");
						u.lastName = document.getString("lastName");
						u.balance = document.getDouble("balance");
						u.accountType = document.getString("accountType");
						tableView.getItems().add(u);
						System.out.println(document.toString());
					}
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		listenData();
	}

	void searchUsers(String name) {
		CollectionReference users = db.collection("users");
		Query query = users.whereGreaterThanOrEqualTo("firstName", name);

		final ApiFuture<QuerySnapshot> future = query.get();
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
						Account u = new Account();
						u.accountId = document.getLong("accountId");
						u.firstName = document.getString("firstName");
						u.lastName = document.getString("lastName");
						u.balance = document.getDouble("balance");
						u.gender = document.getString("gender");
						u.accountType = document.getString("accountType");
						tableView.getItems().clear();
						tableView.getItems().add(u);
						System.out.println(u.toString());
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		});
	}

	void listenData() {
		db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
			@Override
			public void onEvent(QuerySnapshot snapshots, FirestoreException error) {
				if (error != null) {
					System.err.println("Listen failed:" + error);
					return;
				}
				for (DocumentChange document : snapshots.getDocumentChanges()) {
					switch (document.getType()) {
					case ADDED:
						Account u = new Account();
						u.accountId = document.getDocument().getLong("accountId");
						u.firstName = document.getDocument().getString("firstName");
						u.lastName = document.getDocument().getString("lastName");
						u.balance = document.getDocument().getDouble("balance");
						u.gender = document.getDocument().getString("gender");
						u.accountType = document.getDocument().getString("accountType");
						u.approved = document.getDocument().contains("approved") ? document.getDocument().getBoolean("approved") : false;
						u.admin = document.getDocument().contains("admin") ? document.getDocument().getBoolean("admin") : false;
						u.isSuspended = document.getDocument().getBoolean("isSuspended"); 
						u.password = document.getDocument().getString("password");
						tableView.getItems().add(u);
						System.out.println(u.toString());
						break;
					default:
						break;

					}
				}
			}
		});
	}

}






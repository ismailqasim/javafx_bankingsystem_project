import com.google.cloud.firestore.DocumentChange;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Account;

public class Admins {
	Firestore db;
	TableView<Account> tableView;
	
	public Admins() {
		Stage stage = new Stage();
		stage.setTitle("Administrators");
		VBox vBox = new VBox();
		tableView = new TableView<Account>();
		
		TableColumn<Account, String> column1 = 
				new TableColumn<Account, String>("Administrator Name");

		TableColumn<Account, String> column2 = 
				new TableColumn<Account, String>("Account ID");
		
		column1.setCellValueFactory(new 
				PropertyValueFactory<Account, String>("firstName"));
		column2.setCellValueFactory(new 
				PropertyValueFactory<Account, String>("accountId"));

		tableView.getColumns().addAll(column1, column2);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		Scene s = new Scene(vBox);
		vBox.getChildren().addAll(tableView);
		
		stage.setScene(s);
		db = FirestoreClient.getFirestore();
		listenData();
		tableView.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					new UserDetails(
							tableView.getSelectionModel().getSelectedItem().accountId);
				}
			}
			
		});
		stage.showAndWait();
	}
	
	void listenData() {
		db.collection("admins").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
						u.firstName = document.getDocument().getString("name");
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

import com.google.cloud.firestore.DocumentChange;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Transaction;

public class TransactionHistory {
	Firestore db;
	TableView<Transaction> tableView;
	
	public TransactionHistory(long id) {
		Stage stage = new Stage();
		stage.setTitle("Transaction History");
		
		VBox vBox = new VBox();
		tableView = new TableView<Transaction>();
		
		TableColumn<Transaction, String> column1 = 
				new TableColumn<Transaction, String>("Transaction Type");
		TableColumn<Transaction, String> column2 = 
				new TableColumn<Transaction, String>("Transaction Amount");
		TableColumn<Transaction, String> column3 = 
				new TableColumn<Transaction, String>("Transaction Date");
		
		column1.setCellValueFactory(new 
				PropertyValueFactory<Transaction, String>("transactionType"));
		column2.setCellValueFactory(new 
				PropertyValueFactory<Transaction, String>("amount"));
		column3.setCellValueFactory(new 
				PropertyValueFactory<Transaction, String>("transactionDate"));
		
		tableView.getColumns().addAll(column1, column2, column3);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		vBox.setPadding(new Insets(20));
		vBox.getChildren().addAll(tableView);
		
		Scene s = new Scene(vBox);
		stage.setScene(s);
		db = FirestoreClient.getFirestore();

		db.collection("transactions").whereEqualTo("accountId", id).addSnapshotListener(new EventListener<QuerySnapshot>() {
			@Override
			public void onEvent(QuerySnapshot snapshots, 
					FirestoreException error) {
				System.out.println(snapshots.toString());
				if (error != null) {
					System.err.println("Listen failed:" + error);
					return;
				}
				for (DocumentChange document : snapshots.getDocumentChanges()) {
					switch (document.getType()) {
					case ADDED:
						tableView.getItems().add(document.getDocument().toObject(Transaction.class));
					default:
						break;
					}
				}
			}
		});
		stage.showAndWait();
		
	}
	
	
}

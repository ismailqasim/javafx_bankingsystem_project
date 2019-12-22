import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentChange;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.Query;
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
import models.Loan;

public class LoansHistory {
	Firestore db;
	TableView<Loan> tableView;
	long accountId = 0;
	public LoansHistory(long accountId) {
		this.accountId = accountId;
		Stage stage = new Stage();
		stage.setTitle("Loans History");
		VBox vBox = new VBox();
		tableView = new TableView<Loan>();
		
		TableColumn<Loan, String> column2 = 
				new TableColumn<Loan, String>("Loan amount");
		
		TableColumn<Loan, String> column3 = 
				new TableColumn<Loan, String>("Request Date");
		
		TableColumn<Loan, String> column4 = 
				new TableColumn<Loan, String>("Loan Status");
		
		column2.setCellValueFactory(new 
				PropertyValueFactory<Loan, String>("amount"));
		column3.setCellValueFactory(new 
				PropertyValueFactory<Loan, String>("requestDate"));
		column4.setCellValueFactory(new 
				PropertyValueFactory<Loan, String>("status"));
		

		tableView.getColumns().addAll(column2, column3, column4);
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
					new LoanDetails(tableView.getSelectionModel().getSelectedItem());
				}
			}
			
		});
		stage.showAndWait();
	}
	
	public void listenData() {
		Query ref = db.collection("loans");
		if (accountId != 0) {
			ref = ref.whereEqualTo("accountId", accountId);
		} else {
			ref = ref.whereEqualTo("adminStatus", 0);
		}
		
		ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
			@Override
			public void onEvent(QuerySnapshot snapshots, FirestoreException error) {
				if (error != null) {
					System.err.println("Listen failed:" + error);
					return;
				}
				for (DocumentChange document : snapshots.getDocumentChanges()) {
					switch (document.getType()) {
					case ADDED: {
						Loan l = document.getDocument().toObject(Loan.class);
						l.setLoanId(document.getDocument().getId());
						tableView.getItems().add(l);
						break;
					}
					case MODIFIED: {
						Loan l = document.getDocument().toObject(Loan.class);
						for(int i =0; i<tableView.getItems().size(); i++) {
							if (tableView.getItems().get(i).getLoanId().equals(document.getDocument().getId())) {
								tableView.getItems().set(i, l);
							}
						}
					}
					default:
						break;

					}
				}
			}
		});
	}
}

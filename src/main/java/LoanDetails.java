import java.util.Date;
import java.util.concurrent.Executor;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.Loan;
import models.Transaction;

public class LoanDetails {
	Firestore db;
	public LoanDetails(final Loan l) {
		Stage stage = new Stage();
		final VBox vBox = new VBox(10);

		Label l1 = new Label("Loan request date:");
		Label l2 = new Label("Loan amount:");
		Label l3 = new Label("Loan request status:");
		Label l4 = new Label("Account ID:");
		
		TextField t1 = new TextField(l.requestDate.toString());
		TextField t2 = new TextField(l.amount+"");
		final TextField t3 = new TextField(l.getStatus());
		Hyperlink t4 = new Hyperlink(l.accountId+"");

		t1.setDisable(true);
		t2.setDisable(true);
		t3.setDisable(true);
		
		Button btnReject = new Button("Reject loan");
		Button btnApprove = new Button("Approve loan");
		btnReject.setTextFill(Color.RED);
		
		HBox h1 = new HBox(10, l1, t1);
		HBox h2 = new HBox(10, l2, t2);
		HBox h3 = new HBox(10, l3, t3);
		HBox h4 = new HBox(10, l4, t4);
		final HBox h5 = new HBox(10, btnReject, btnApprove);
		h1.setAlignment(Pos.CENTER_LEFT);
		h2.setAlignment(Pos.CENTER_LEFT);
		h3.setAlignment(Pos.CENTER_LEFT);
		h4.setAlignment(Pos.CENTER_LEFT);
		h5.setAlignment(Pos.BASELINE_CENTER);
		t4.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!Constant.ADMIN) return;
				new UserDetails(l.accountId);
				
			}
		});
		
		vBox.getChildren().addAll(h1, h2, h3, h4);
		if (Constant.ADMIN 
				&& l.adminStatus == 0) {
			vBox.getChildren().add(h5);
		}
		vBox.setPadding(new Insets(10));
		
		Scene s = new Scene(vBox);
		stage.setScene(s);
		db = FirestoreClient.getFirestore();
		
		btnReject.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DocumentReference docRef = db.collection("loans").document(l.getLoanId());
				final ApiFuture<WriteResult> future = docRef.update("adminStatus", 2);
				future.addListener(new Runnable() {
					@Override
					public void run() {
					}
				}, new Executor() {
					@Override
					public void execute(Runnable command) {
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {
								Alert alert = new Alert(AlertType.INFORMATION);
								alert.setContentText("Loan request denied!");
								alert.show();
								
								if (vBox.getChildren().contains(h5)) {
									vBox.getChildren().remove(h5);
								}
							}
						});
						l.adminStatus = 2;
						t3.setText(l.getStatus());

					}
				});
			}
		});
		
		btnApprove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DocumentReference docRef = db.collection("loans").document(l.getLoanId());
				final ApiFuture<WriteResult> future = docRef.update("adminStatus", 1);
				future.addListener(new Runnable() {
					@Override
					public void run() {
					}
				}, new Executor() {
					@Override
					public void execute(Runnable command) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								
								DocumentReference docRef = db.collection("users").document(l.accountId+"");
								final ApiFuture<WriteResult> future = docRef.update("balance", FieldValue.increment(l.amount));
								
								Transaction t = new Transaction();
								t.accountId = l.accountId;
								t.amount = l.amount;
								t.transactionType = "Debit";
								t.transactionId = 0;
								t.transactionDate = new Date();
								
								db.collection("transactions").add(t);
			
								Alert alert = new Alert(AlertType.INFORMATION);
								alert.setContentText("Loan request approved!");
								alert.show();
								
								if (vBox.getChildren().contains(h5)) {
									vBox.getChildren().remove(h5);
								}
							}
						});
						l.adminStatus = 1;
						t3.setText(l.getStatus());
					}
				});
			}
		});
		
		stage.showAndWait();
	}
}

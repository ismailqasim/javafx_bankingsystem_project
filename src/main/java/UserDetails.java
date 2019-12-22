import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentChange;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.Account;

public class UserDetails {
	Firestore db;
	Account account;
	
	TextField t1;
	TextField t2;
	ComboBox<String> c1;
	TextField t4, t5;
	PasswordField t3;
	ComboBox<String> c2;
	
	Button btnSave, btnSuspend, btnAdmin, btnTransactions, btnRequests;
	
	private UserDetails() {
		Stage stage = new Stage();
		stage.setTitle("Account details");
		VBox vBox = new VBox(10);
		
		Label l1 = new Label("First name:");
		Label l2 = new Label("Last name:");
		Label l3 = new Label("Account Type:");
		Label l4 = new Label("Password:");
		Label l5 = new Label("Gender:");
		Label l6 = new Label("Account status:");
		Label l7 = new Label("Balance:");
		
		t1 = new TextField();
		t2 = new TextField();
		c1 = new ComboBox<String>();
		c1.getItems().addAll("Current account", "Saving account");
		t4 = new TextField();
		t4.setDisable(true);
		t3 = new PasswordField();
		c2 = new ComboBox<String>();
		c2.getItems().addAll("Approved", "Not approved");
		t5 = new TextField();

		HBox h1 = new HBox(10, l1, t1);
		HBox h2 = new HBox(10, l2, t2);
		HBox h3 = new HBox(10, l3, c1);
		HBox h4 = new HBox(10, l4, t3);
		HBox h5 = new HBox(10, l5, t4);
		HBox h6 = new HBox(10, l6, c2);
		HBox h7 = new HBox(10, l7, t5);
		
		btnSave = new Button("Save changes");
		btnSave.setDisable(true);
		btnSuspend = new Button("Suspend account");
		btnSuspend.setDisable(true);
		btnAdmin = new Button("Assign admin");
		btnAdmin.setDisable(true);
		btnTransactions = new Button("View transactions");
		btnTransactions.setDisable(true);
		btnRequests = new Button("View loan request");
		btnRequests.setDisable(true);
		
		btnRequests.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				new LoansHistory(account.accountId);
			}
		});
		
		btnSuspend.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				DocumentReference docRef = db.collection("users").document(""+account.accountId);
				final ApiFuture<WriteResult> future = docRef.update("isSuspended", account.isSuspended ? false : true);
				future.addListener(new Runnable() {
					@Override
					public void run() {}
				}, new Executor() {
					@Override
					public void execute(Runnable command) {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								Alert alert = new Alert(AlertType.INFORMATION);
								alert.setHeaderText(account.isSuspended ? "Account is no longer suspended" : "Account has been suspended");
								alert.setContentText(account.isSuspended ? "Account " + account.accountId + " is no longer suspended and can be logged in." :
									"Account " + account.accountId + " can no longer be used");
								account.isSuspended = !account.isSuspended;
								btnSuspend.setText(account.isSuspended ? "Rmove suspension" : "Suspend account");
								
								DocumentReference stats = db.collection("info").document("stats");
								if (account.isSuspended) {
									stats.update("suspended", FieldValue.increment(1));
								} else {
									stats.update("suspended", FieldValue.increment(-1));
								}
								
								alert.showAndWait();
							}
						});
					}
				});
			}
		});

		btnTransactions.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Button transactions");
				new TransactionHistory(account.accountId);
			}
		});

		btnAdmin.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DocumentReference docRef = db.collection("users").document(""+account.accountId);
				final ApiFuture<WriteResult> future = docRef.update("admin", account.admin ? false : true);
				future.addListener(new Runnable() {
					@Override
					public void run() {}
				}, new Executor() {
					@Override
					public void execute(Runnable command) {
						account.admin = !account.admin;
						
						if (account.admin) {
							Map<String, Object> docData = new HashMap<>();
							docData.put("name", account.firstName + " " + account.lastName);
							docData.put("accountId", account.accountId);
							docData.put("password", account.password);
							ApiFuture<DocumentReference> future = db.collection("admins").add(docData);
						} else {
							db.collection("admins").whereEqualTo("accountId", account.accountId).addSnapshotListener(new EventListener<QuerySnapshot>() {
						
								@Override
								public void onEvent(QuerySnapshot value, FirestoreException error) {
									if (error != null) {
										System.err.println("Listen failed:" + error);
										return;
									}
									for (QueryDocumentSnapshot document : value.getDocuments()) {
										db.collection("admins").document(document.getId()).delete();
									}
								}
							});
						}
					
						Platform.runLater(new Runnable() {

							@Override
							public void run() {

								Alert alert = new Alert(AlertType.INFORMATION);
								alert.setHeaderText(account.admin ? "Admin assigned" : "Admin right removed");
								alert.setContentText(account.admin ? "Account " + account.accountId + " has been assigned with admin privileges." :
									"Account " + account.accountId + " admin rights revoked.");
								btnAdmin.setText(account.admin ? "Revoke admin" : "Assign admin");
								alert.showAndWait();
							}
						});
					}
				});
			}
		});

		
		btnSuspend.setTextFill(Color.RED);
		HBox footer = new HBox(10, btnSuspend, btnSave, btnAdmin, btnTransactions, btnRequests);

		vBox.getChildren().addAll(h1, h2, h3, h4, h5, h6, h7, footer);
		vBox.setPadding(new Insets(10));

		Scene s = new Scene(vBox);
		stage.setScene(s);
		db = FirestoreClient.getFirestore();
		stage.show();
	}
	
	public UserDetails(final Account account) {
		this();
		this.account = account;
		loadData();
	}
	
	public UserDetails(long accountId) {
		this();
		DocumentReference docRef = db.collection("users").document(accountId+"");
		final ApiFuture<DocumentSnapshot> future = docRef.get();
		future.addListener(new Runnable() {
			@Override
			public void run() {
			}
		}, new Executor() {
			@Override
			public void execute(Runnable command) {
				try {
					DocumentSnapshot document = future.get();
					Account u = new Account();
					u.accountId = document.getLong("accountId");
					u.firstName = document.getString("firstName");
					u.lastName = document.getString("lastName");
					u.gender = document.getString("gender");
					u.balance = document.getDouble("balance");
					u.accountType = document.getString("accountType");
					u.approved = document.contains("approved") ? document.getBoolean("approved") : false;
					u.admin = document.contains("admin") ? document.getBoolean("admin") : false;
					u.isSuspended = document.getBoolean("isSuspended"); 
					u.password = document.getString("password");
					UserDetails.this.account = u;
					loadData();
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}
	
	void loadData() {
	
		btnSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				final boolean changes = account.approved != (c2.getSelectionModel().getSelectedIndex() == 0);
				
				account.firstName = t1.getText();
				account.lastName = t2.getText();
				account.accountType = c1.getSelectionModel().getSelectedItem();
				account.password = t3.getText();
				account.approved = c2.getSelectionModel().getSelectedIndex() == 0;
				try {
					account.balance = Double.parseDouble(t5.getText());
				} catch (NumberFormatException ex) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("Please enter valid balance");
					alert.show();
					ex.printStackTrace();
					return;
				}
				DocumentReference docRef = db.collection("users").document(""+account.accountId);
				final ApiFuture<WriteResult> future = docRef.set(account);
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
								if (changes) {
									DocumentReference stats = db.collection("info").document("stats");
									stats.update("requests", account.approved ? FieldValue.increment(-1) : FieldValue.increment(1));
								}
								
								Alert alert = new Alert(AlertType.INFORMATION);
								alert.setContentText("Changes have been successfully made!");
								alert.showAndWait();
							}
						});
					}
				});
			}
		});
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				btnSave.setDisable(false);
				btnSuspend.setDisable(false);
				btnAdmin.setDisable(false);
				btnTransactions.setDisable(false);
				btnRequests.setDisable(false);
				btnSuspend.setText(account.isSuspended ? "Rmove suspension" : "Suspend account");
				btnAdmin.setText(account.admin ? "Revoke admin" : "Assign admin");
				
				t1.setText(account.getFirstName());
				t2.setText(account.getLastName());
				c1.getSelectionModel().select(account.getAccountType());
				t3.setText(account.password);
				t4.setText(account.gender);
				c2.getSelectionModel().select(account.approved ? 0 : 1);
				t5.setText(account.balance+"");
			}
		});
	}
}

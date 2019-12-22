import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.firestore.DocumentChange;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import io.grpc.StatusRuntimeException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.Account;
import models.Loan;
import models.Transaction;

public class Dashboard {
	Font navFont = new Font(16);
	Firestore db;
	DocumentSnapshot data;
	
	Label info2;
	Label info3;
	Label info4;
	Label accName;
	
	Stage stage;
	TabPane body;
	
	private ArrayList<onDashboardListener> listener = new ArrayList<>();
	interface onDashboardListener {
		void onDocumentChange(DocumentSnapshot snapshot);
	}
	public void addListener(onDashboardListener listener) {
		System.out.println("Listener added at " + body.getTabs().size());
		this.listener.add(listener);
	}
	
	public Dashboard(DocumentSnapshot data) {
		Constant.ADMIN = false;
		db = FirestoreClient.getFirestore();
		this.data = data;
		listenData();
		
		stage = new Stage();
		stage.setTitle("AIAS Banking - Dashboard");
		stage.setResizable(false);
		
		VBox vBox = new VBox();
		HBox header = new HBox(20);
		header.setPrefHeight(100);
		header.setAlignment(Pos.CENTER);
		Label title = new Label("AIAS Banking");
		title.setFont(new Font(30));
		
		FileInputStream input;
		Image image;
		ImageView imageView = new ImageView();
		imageView.setFitWidth(25);
		imageView.setFitHeight(25);
		try {
			input = new FileInputStream("src/main/resources/user.png");
			image = new Image(input);
			imageView.setImage(image);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		accName = new Label(data.getString("firstName")+" "+
		data.getString("lastName"));
		accName.setFont(new Font(24));

		
		Region region= new Region();
		header.setHgrow(region, Priority.ALWAYS);
		
		Separator separator = new Separator();
		
		Button btnLogout = new Button("Logout");
		
		btnLogout.setFont(new Font(15));
		btnLogout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.close();
				new LoginPage();
			}
		});
		
		HBox hBox = new HBox();
		VBox nav = new VBox(10);
		body = new TabPane();
		VBox info = new VBox(10);
		
		Label info1= new Label("Current Account Summary");
		Separator sep1= new Separator();
		
		info2= new Label("Account ID: "+ data.getLong("accountId"));
		Separator sep2= new Separator();
			
		info3= new Label("Balance: Rs. "+ data.getDouble("balance"));
		Separator sep3= new Separator();
		
		info4= new Label("Account type: " + data.getString("accountType"));
		Separator sep4= new Separator();
		
		info.setPadding(new Insets(10));
		
		info.getChildren().addAll(info1,sep1,info2,sep2,info3,sep3,info4,sep4);
		
		
		Region r2 = new Region();
		r2.setPrefWidth(10);
		
		Region r3 = new Region();
		r3.setPrefWidth(10);
		Button btnAccDetails=new Button("Account Details");
		btnAccDetails.setPrefWidth(180);
		btnAccDetails.setFont(navFont);
		btnAccDetails.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				int exist = tabExist("Account details");
				if (exist == -1) {
					final Tab tab = new Tab("Account details", accountDetails());
					tab.setUserData(body.getTabs().size());
					tab.setOnCloseRequest(new EventHandler<Event>() {
						@Override
						public void handle(Event event) {
							// TODO Auto-generated method stub

							System.out.println("Listener removed at " + body.getSelectionModel().getSelectedIndex() + ", size: " + listener.size());
							listener.remove(body.getSelectionModel().getSelectedIndex());
						}
					});
					body.getTabs().add(tab); 
					body.getSelectionModel().select(tab);
				} else {
					body.getSelectionModel().select(exist);
				}
			}
		});
		
		Button btnLoan=new Button("Loans");
		btnLoan.setPrefWidth(180);
		btnLoan.setFont(navFont);
		btnLoan.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int exist = tabExist("Loan");
				if (exist == -1) {
					final Tab tab = new Tab("Loan", loansTab());
					tab.setUserData(body.getTabs().size());
					tab.setOnCloseRequest(new EventHandler<Event>() {
						@Override
						public void handle(Event event) {
							// TODO Auto-generated method stub

							System.out.println("Listener removed at " + body.getSelectionModel().getSelectedIndex() + ", size: " + listener.size());
							listener.remove(body.getSelectionModel().getSelectedIndex());
						}
					});
					body.getTabs().add(tab); 
					body.getSelectionModel().select(tab);
				} else {
					body.getSelectionModel().select(exist);
				}
			}
		});
		
		Button btnTransfer=new Button("Transfer Cash");
		btnTransfer.setPrefWidth(180);
		btnTransfer.setFont(navFont);
		btnTransfer.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int exist = tabExist("Transfer Cash");
				if (exist == -1) {
					final Tab tab = new Tab("Transfer Cash", transferTab());
					tab.setUserData(body.getTabs().size());
					tab.setOnCloseRequest(new EventHandler<Event>() {
						@Override
						public void handle(Event event) {
							System.out.println("Listener removed at " + body.getSelectionModel().getSelectedIndex() + ", size: " + listener.size());
							listener.remove(body.getSelectionModel().getSelectedIndex());
						}
					});
					body.getTabs().add(tab); 
					body.getSelectionModel().select(tab);
				} else {
					body.getSelectionModel().select(exist);
				}
			}
		});
		
		Button btnHistory=new Button("Transaction History");
		btnHistory.setPrefWidth(180);
		btnHistory.setFont(navFont);
		btnHistory.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int exist = tabExist("Transaction History");
				if (exist == -1) {
					final Tab tab = new Tab("Transaction History", transactionTab());
					tab.setUserData(body.getTabs().size());
					tab.setOnCloseRequest(new EventHandler<Event>() {
						@Override
						public void handle(Event event) {
							System.out.println("Listener removed at " + body.getSelectionModel().getSelectedIndex() + ", size: " + listener.size());
							listener.remove(body.getSelectionModel().getSelectedIndex());
						}
					});
					body.getTabs().add(tab); 
					body.getSelectionModel().select(tab);
				} else {
					body.getSelectionModel().select(exist);
				}
			}
		});
		
		Button btnStatement=new Button("Bank Statement");
		btnStatement.setPrefWidth(180);
		btnStatement.setFont(navFont);
		btnStatement.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int exist = tabExist("Bank Statement");
				if (exist == -1) {
					final Tab tab = new Tab("Bank Statement", statementTab());
					tab.setUserData(body.getTabs().size());
					tab.setOnCloseRequest(new EventHandler<Event>() {
						@Override
						public void handle(Event event) {
							System.out.println("Listener removed at " + body.getSelectionModel().getSelectedIndex() + ", size: " + listener.size());
							listener.remove(body.getSelectionModel().getSelectedIndex());
						}
					});
					body.getTabs().add(tab); 
					body.getSelectionModel().select(tab);
				} else {
					body.getSelectionModel().select(exist);
				}
			}
		});
		
		Separator separator1 = new Separator(Orientation.VERTICAL);
		
		nav.setAlignment(Pos.TOP_CENTER);
		nav.setPrefWidth(200);
		nav.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY,Insets.EMPTY)));
		
		nav.getChildren().addAll(info, btnAccDetails,btnLoan,
				btnTransfer,btnHistory,btnStatement);
		nav.setPrefHeight(400);
		
		hBox.getChildren().addAll(nav,separator1,body);
		hBox.setHgrow(body, Priority.ALWAYS);
		
		header.getChildren().addAll(r2,title,region,imageView,accName,btnLogout,r3);
		vBox.getChildren().addAll(header, separator, hBox);
		
		Scene scene = new Scene(vBox, 800, 500);
		stage.setScene(scene);
		stage.show();
		
		
		
	}
	
	int tabExist(String text) {
		for (int i = 0; i <= body.getTabs().size() - 1; i++) {
			if (body.getTabs().get(i).getText().equals(text)) {
				return i;
			}
		}
		return -1;
	}
	
	VBox accountDetails(){
		VBox adBox= new VBox(10);
		Label l1= new Label("Account details");
		l1.setFont(new Font(24));
		
		Label l2= new Label("Account ID: " + data.getLong("accountId"));
		l2.setFont(new Font(16));
		
		final Label l3= new Label("First name: " + data.getString("firstName"));
		l3.setFont(new Font(16));
		
		final Label l4= new Label("Last name: " + data.getString("lastName"));
		l4.setFont(new Font(16));
		
		final Label l5= new Label("Gender: " + data.getString("gender"));
		l5.setFont(new Font(16));
		
		final Label l6= new Label("Last Transaction: Not yet");
		l6.setFont(new Font(16));
		
		final Label l7= new Label("Account type: " + data.getString("accountType"));
		l7.setFont(new Font(16));
			
		Label l8= new Label("Joined date: Today");
		l8.setFont(new Font(16));
	
		Separator sepL1 = new Separator();
		Separator sepL2 = new Separator();
		Separator sepL3 = new Separator();
		Separator sepL4 = new Separator();
		Separator sepL5 = new Separator();
		Separator sepL6 = new Separator();
		Separator sepL7 = new Separator();
	
		adBox.setPadding(new Insets(20));
		adBox.getChildren().addAll(l1,sepL1,l2,sepL2,l3,sepL3,
				l4,sepL4,l5,sepL5,l6,sepL6,l7,sepL7,l8);
	
		addListener(new onDashboardListener() {
			@Override
			public void onDocumentChange(DocumentSnapshot snapshot) {
				// TODO Auto-generated method stub
				System.out.println("Dashboard: " + snapshot.getData());
				l3.setText("First name: " + data.getString("firstName"));
				l4.setText("Last name: " + data.getString("lastName"));
				l5.setText("Gender: " + data.getString("gender"));
				l6.setText("Last Transaction: Not yet");
				l7.setText("Account type: " + data.getString("accountType"));
			}
		});
		
		return adBox;
	}
	
	VBox loansTab() {
		VBox adBox= new VBox(10);
		HBox h1= new HBox(10);
		h1.setAlignment(Pos.CENTER_LEFT);
		Label labelLoan = new Label("How much loan you would like to get from bank?");
		labelLoan.setFont(new Font(18));
		final TextField loanText = new TextField();
		Label label1 = new Label("Rs. ");
		Label label2 = new Label("(Rs. 100 - Rs. 1000000)");
        Button loanBtn = new Button("Request Loan");
        Button btnHistory = new Button("View loan requests");
        HBox hBox = new HBox(10, loanBtn, btnHistory);
		adBox.getChildren().addAll(labelLoan,h1,hBox);
		adBox.setPadding(new Insets(20));
		h1.getChildren().addAll(label1,loanText,label2);
		
		btnHistory.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				new LoansHistory(data.getLong("accountId"));
			}
		});
		loanBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					double amount = Double.parseDouble(loanText.getText());
					if (amount >=100 && amount <=1000000){
						Loan l = new Loan();
						l.accountId = data.getLong("accountId");
						l.amount = amount;
						l.requestDate = new Date();
						l.adminStatus = 0;
						final ApiFuture<DocumentReference> future = db.collection("loans").add(l);
						future.addListener(new Runnable() {
							@Override
							public void run() {
								
							}
						}, new Executor() {
							@Override
							public void execute(Runnable arg0) {
								Platform.runLater(new Runnable() {
									
									@Override
									public void run() {

										Alert alert= new Alert(AlertType.INFORMATION);
										alert.setContentText("Loan request has been sent to bank. Please wait for their approval");
										alert.showAndWait();
										loanText.setText("");
									}
								});
							}
						});
					}
					else {
						Alert alert= new Alert(AlertType.ERROR);
						alert.setContentText("The amount must be above 100 Rupees and within 1 million");
						alert.showAndWait();
					}
				}
				catch(NumberFormatException e){
					Alert alert= new Alert(AlertType.ERROR);
					alert.setContentText("Invalid Entry");
					alert.showAndWait();
					
				}
			
				
			}
		});
		
		
		addListener(new onDashboardListener() {
			@Override
			public void onDocumentChange(DocumentSnapshot snapshot) {
				System.out.println("Loan: " + snapshot.getData());
				
			}
		});
		return adBox;
	}
	
	VBox transferTab() {
		VBox adBox= new VBox(10);
		Label l1= new Label("Transfer Cash");
		l1.setFont(new Font(24));
		Separator s1 = new Separator();
		
		Label l2 = new Label("Enter the account number: ");
		final TextField t1 = new TextField();
		HBox h1 = new HBox(10, l2, t1);
		h1.setAlignment(Pos.CENTER_LEFT);
		Separator s2 = new Separator();
		
		Label l3 = new Label("Enter the amount to transfer: ");
		final TextField t2 = new TextField();
		HBox h2 = new HBox(10, l3, t2);
		h2.setAlignment(Pos.CENTER_LEFT);
		Separator s3 = new Separator();
		
		Button btnTransfer = new Button("Make transaction");
		btnTransfer.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				final double amount = Double.parseDouble(t2.getText());
				final long account = Long.parseLong(t1.getText());
				if (data.getDouble("balance") >= amount) {
					DocumentReference docRef = db.collection("users").document(account+"");
					final ApiFuture<WriteResult> future = docRef.update("balance", FieldValue.increment(amount));
					future.addListener(new Runnable() {
						@Override
						public void run() {
							
						}
					}, new Executor() {
						@Override
						public void execute(Runnable command) {
							try {
								WriteResult result = future.get();
								DocumentReference docRef = db.collection("users").document(data.getId());
								docRef.update("balance", FieldValue.increment(-amount));
								System.out.println("Write result: " + result);
								
								Transaction t = new Transaction();
								t.accountId = data.getLong("accountId");
								t.amount = amount;
								t.transactionType = "Debit";
								t.transactionId = 0;
								t.transactionDate = new Date();
								
								db.collection("transactions").add(t);
								
								Transaction t2 = new Transaction();
								t.accountId = account;
								t.amount = amount;
								t.transactionType = "Credit";
								t.transactionId = 0;
								t.transactionDate = new Date();
								
								db.collection("transactions").add(t);
								
								DocumentReference stats = db.collection("info").document("stats");
								stats.update("transactions", FieldValue.increment(1));
								
							}
							catch (InterruptedException | ExecutionException e) {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										Alert alert = new Alert(AlertType.ERROR);
										alert.setContentText("The account number you entered is not valid");
										alert.showAndWait();
									}
								});
							} 
						}
					});
				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("You don't have sufficient balance");
					alert.showAndWait();
				}
			}
		});
		
		adBox.setPadding(new Insets(20));
		adBox.getChildren().addAll(l1,s1,h1, s2, h2, s3, btnTransfer);
		
		addListener(new onDashboardListener() {
			@Override
			public void onDocumentChange(DocumentSnapshot snapshot) {
				System.out.println("Transaction: " + snapshot.getData());
				
			}
		});
		return adBox;
	}
	
	VBox transactionTab() {
		VBox adBox= new VBox(10);
		addListener(new onDashboardListener() {
			@Override
			public void onDocumentChange(DocumentSnapshot snapshot) {
				System.out.println("Transaction: " + snapshot.getData());
				
			}
		});

		final TableView<Transaction> tableView = new TableView<Transaction>();
		db.collection("transactions").whereEqualTo("accountId", data.getLong("accountId")).addSnapshotListener(new EventListener<QuerySnapshot>() {
			@Override
			public void onEvent(QuerySnapshot snapshots, 
					FirestoreException error) {
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

		adBox.setPadding(new Insets(20));
		adBox.getChildren().addAll(tableView);
		
		return adBox;
	}
	
	VBox statementTab() {
		VBox adBox= new VBox(10);
		addListener(new onDashboardListener() {
			@Override
			public void onDocumentChange(DocumentSnapshot snapshot) {
				System.out.println("Transaction: " + snapshot.getData());
				
			}
		});
		return adBox;
	}
	
	void listenData() {
		DocumentReference docRef = db.collection("users").document(data.getId());
		docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
			@Override
			public void onEvent(final DocumentSnapshot doc, FirestoreException e) {
				// TODO Auto-generated method stub
				if (e != null) {
					System.err.println("Listen failed: " + e);
					return;
				}
				if (doc != null && doc.exists()) {
					final double lastAmount = data.getDouble("balance");
					data = doc;
				
					Platform.runLater(new Runnable() {
	
						@Override
						public void run() {
							if (data.getBoolean("isSuspended")) {
								System.out.println("Account suspended!");
								
								Alert alert = new Alert(AlertType.ERROR);
								alert.setHeaderText("Account suspended!");
								alert.setContentText("Your account has been suspended!");
								
								alert.setOnHidden(new EventHandler<DialogEvent>() {
									@Override
									public void handle(DialogEvent event) {
										stage.close();
										new LoginPage();
									}
								});
								alert.showAndWait();
							} else {
								info2.setText("Account ID: "+ data.getLong("accountId"));
								info3.setText("Balance: Rs. "+ data.getDouble("balance"));
								info4.setText("Account type: " + data.getString("accountType"));
								accName.setText(data.getString("firstName")+" "+
										data.getString("lastName"));
								
								if (data.getDouble("balance") != lastAmount) {
									double a = lastAmount - data.getDouble("balance");
									Alert alert = new Alert(AlertType.INFORMATION);
									if (a < 0) {
										// credit
										alert.setHeaderText("Amount credited");
										alert.setContentText("Rs. " + (a * -1) + " has been credited into your account.");
									} else {
										// debit
										alert.setHeaderText("Amount debited");
										alert.setContentText("Rs. " + (a) + " has been debited from your account.");
									}
									alert.showAndWait();
								}
								
								if (listener != null) {
									for(int i = 0; i < listener.size(); i++) {
										listener.get(i).onDocumentChange(doc);
									}
								}
							}
						}
						
					});
				
				
					
					System.out.println("Current data: " + doc.getData());
				}
			}
		});
	}

}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrestling.view.controller;

import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import wrestling.model.Worker;

/**
 *
 * @author jared
 */
public class WorkerCell extends ListCell<Worker> {
                    
          private TextField textField ;
          
          private final EventHandler<MouseEvent> dragDetectedHandler ;
          
          WorkerCell() {               
               this.dragDetectedHandler = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        
                        System.out.println("allprojectlistcell handle");
                        
                         // Set up dummy data on the dragboard else drag and drop won't be initiated
                         Dragboard db = startDragAndDrop(TransferMode.MOVE);
                         ClipboardContent cc = new ClipboardContent();
                         cc.putString(getItem().toString());
                         db.setContent(cc);
                         /*
                         we need to drag the reference to the worker, not just the string
                         */
                         LocalDragboard.getInstance().putValue(Worker.class, getItem());
                         
                         
                         
                         event.consume();
                    }
               };
               
          }
          
          @Override
          public void updateItem(final Worker worker, boolean empty) {
               super.updateItem(worker, empty);
               if (empty) {
                    setText(null);
                    setGraphic(null);
                    setOnDragDetected(null);
               } else if (isEditing()) {
                    if (textField != null) {
                         textField.setText(getItem().toString());
                    }
                    setText(null) ;
                    setOnDragDetected(null);
                    setGraphic(textField);                    
               } else {
                    setText(worker.toString());
                    setOnDragDetected(dragDetectedHandler);
               }
          }
    }
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dc.file.search.ui;

import com.google.gson.Gson;
import org.apache.commons.lang.RandomStringUtils;
import org.dc.file.search.Constants.MessageType;
import org.dc.file.search.MessageUtils;
import org.dc.file.search.SearchRequest;
import org.dc.file.search.SearchResult;
import org.dc.file.search.Store;
import org.dc.file.search.dto.Comment;
import org.dc.file.search.dto.DFile;
import org.dc.file.search.dto.Peer;
import org.dc.file.search.dto.Rating;

import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import static org.dc.file.search.ui.DashboardForm.resultFiles;
import static org.dc.file.search.ui.DashboardForm.selectedFile;

/**
 * @author rasikaperera
 */
public class DashboardForm extends javax.swing.JFrame {

    private static final int DEFAULT_SEARCH_HOPE_COUNT = 2;
    private static final int DEFAULT_SEARCH_TIMEOUT_SEC = 5;
    private boolean advancedSearchEnabled = false;

    public final static int PEER_COL_INDEX = 0;
    public final static int HOP_COUNT_COL_INDEX = 1;
    public final static int FILE_COL_INDEX = 2;
    public final static int STAR_RATINGS_COL_INDEX = 3;

    public final static int COMMENT_COL_INDEX = 0;
    public final static int COMMENT_RATING_COL_INDEX = 2;
    public final static int COMMENT_REPLY_COL_INDEX = 3;

    private final static int UUID_LEN = 8;
    private final static int MAX_COLS = 4;

    private final String[] searchColumnNames = {"Peer", "Hop Count", "File", "Ratings"};
    private final String[] commentColumnNames = {"id", "Comment", "Ratings", "Reply"};

    public static int MAX_RATING = 5;

    static volatile Map<String, DFile> resultFiles;
    static volatile String selectedFile = "";
    public enum StarRatingsType {COMMENT, FILE, DEFAULT}

    /**
     * Creates new form DashboardFormNew
     */
    public DashboardForm() {
        initComponents();
        setLocationRelativeTo(null);

        setVisibleAdvancedSearchPanel(false);
        tblSearchResults.setVisible(false);
        progressBar.setVisible(false);

        initSearchResultsTable();
        initCommentResultsTable();

        Peer localPeer = Store.getInstance().getLocalPeer();
        setTitle("Dashboard :" + localPeer.getUsername() + "(" + localPeer.getKey() + ")");
    }

    private void initCommentResultsTable(){
        Object[][] initData = {};
        DefaultTableModel commentModel = new DefaultTableModel(initData, commentColumnNames);
        tblComments.setModel(commentModel);
        tblComments.setRowHeight(32);

        TableColumn commentRatingColumn = tblComments.getColumnModel().getColumn(COMMENT_RATING_COL_INDEX);
        commentRatingColumn.setCellRenderer(new StarRatingsRenderer(tblComments, StarRatingsType.COMMENT));
        commentRatingColumn.setCellEditor(new StarRatingsEditor(tblComments, StarRatingsType.COMMENT));
        commentRatingColumn.setPreferredWidth(30);

        TableColumn commentReply = tblComments.getColumnModel().getColumn(COMMENT_REPLY_COL_INDEX);
        commentReply.setCellRenderer(new ButtonRenderer(tblComments));
        commentReply.setCellEditor(new ButtonEditor(tblComments));
    }

    private void initSearchResultsTable(){
        Object[][] initData = {};
        DefaultTableModel searchModel = new DefaultTableModel(initData, searchColumnNames);
        tblSearchResults.setModel(searchModel);
        tblSearchResults.setRowHeight(32);

        tblSearchResults.getColumnModel().getColumn(PEER_COL_INDEX).setPreferredWidth(30);
        tblSearchResults.getColumnModel().getColumn(HOP_COUNT_COL_INDEX).setPreferredWidth(20);
        tblSearchResults.getColumnModel().getColumn(FILE_COL_INDEX).setPreferredWidth(20);

        TableColumn starRatingsColumn = tblSearchResults.getColumnModel().getColumn(STAR_RATINGS_COL_INDEX);
        starRatingsColumn.setCellRenderer(new StarRatingsRenderer(tblSearchResults, StarRatingsType.FILE));
        starRatingsColumn.setCellEditor(new StarRatingsEditor(tblSearchResults, StarRatingsType.FILE));
        starRatingsColumn.setPreferredWidth(30);

        tblSearchResults.getSelectionModel().addListSelectionListener(event -> {
            if(tblSearchResults.getSelectedRow() < 0){return;}
            initCommentResultsTable();
            DefaultTableModel model = (DefaultTableModel) tblComments.getModel();
            model.setRowCount(0);

            selectedFile = tblSearchResults.getValueAt(tblSearchResults.getSelectedRow(), FILE_COL_INDEX).toString();
            DFile dFile = resultFiles.get(selectedFile);
            for (int i = 0; i < dFile.getComments().size(); i++) {
                Object[] data = new Object[MAX_COLS];
                data[0] = dFile.getComments().get(i).getCommentId();
                data[1] = dFile.getComments().get(i).getText();
                data[2] = new StarRater(5, dFile.getComments().get(i).getTotalRating(), 0);
                data[3] = new ButtonRenderer(tblComments);
                model.addRow(data);
            }
            tblComments.setModel(model);
            model.fireTableDataChanged();
        });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtSearchKey = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSearchResults = new javax.swing.JTable();
        btnFilesList = new javax.swing.JButton();
        btnPeersList = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        chkAdvancedSearch = new javax.swing.JCheckBox();
        lblHopCount = new javax.swing.JLabel();
        txtHopCount = new javax.swing.JTextField();
        lblTimeout = new javax.swing.JLabel();
        txtTimeoutSec = new javax.swing.JTextField();
        lblSec = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        sliderHopCount = new javax.swing.JSlider();
        sliderTimout = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        btnNewComment = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblComments = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtCommentThread = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        tblSearchResults.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Peer", "Hop Count", "File", "Ratings"
            }
        ));
        jScrollPane1.setViewportView(tblSearchResults);

        btnFilesList.setText("Files List");
        btnFilesList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilesListActionPerformed(evt);
            }
        });

        btnPeersList.setText("Peers List");
        btnPeersList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPeersListActionPerformed(evt);
            }
        });

        jLabel1.setText("File Name:");

        chkAdvancedSearch.setText("Enable Advanced Search");
        chkAdvancedSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAdvancedSearchActionPerformed(evt);
            }
        });

        lblHopCount.setLabelFor(txtHopCount);
        lblHopCount.setText("Hop Count:");

        txtHopCount.setEditable(false);
        txtHopCount.setText("2");
        txtHopCount.setToolTipText("");

        lblTimeout.setLabelFor(txtTimeoutSec);
        lblTimeout.setText("Timeout:");

        txtTimeoutSec.setEditable(false);
        txtTimeoutSec.setText("5");

        lblSec.setText("sec");

        sliderHopCount.setMaximum(20);
        sliderHopCount.setMinimum(2);
        sliderHopCount.setValue(2);
        sliderHopCount.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderHopCountStateChanged(evt);
            }
        });

        sliderTimout.setMaximum(60);
        sliderTimout.setMinimum(5);
        sliderTimout.setMinorTickSpacing(5);
        sliderTimout.setValue(5);
        sliderTimout.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderTimoutStateChanged(evt);
            }
        });

        jLabel2.setText("Comments");

        btnNewComment.setText("New Comment");
        btnNewComment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewCommentActionPerformed(evt);
            }
        });

        tblComments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id", "Comment", "Ratings", "Reply"
            }
        ));
        jScrollPane3.setViewportView(tblComments);
        if (tblComments.getColumnModel().getColumnCount() > 0) {
            tblComments.getColumnModel().getColumn(0).setPreferredWidth(250);
        }

        txtCommentThread.setColumns(20);
        txtCommentThread.setRows(5);
        jScrollPane4.setViewportView(txtCommentThread);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSearchKey)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnNewComment))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnFilesList)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPeersList))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lblHopCount)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtHopCount, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sliderHopCount, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTimeout)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTimeoutSec, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(lblSec)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(sliderTimout, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5))))
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkAdvancedSearch)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch)
                    .addComponent(txtSearchKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkAdvancedSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblHopCount)
                        .addComponent(txtHopCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTimeout)
                        .addComponent(txtTimeoutSec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSec))
                    .addComponent(sliderHopCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sliderTimout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNewComment)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFilesList)
                    .addComponent(btnPeersList))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        int hopCount = DEFAULT_SEARCH_HOPE_COUNT;
        int timeout = DEFAULT_SEARCH_TIMEOUT_SEC;

        if (advancedSearchEnabled) {
            try {
                hopCount = Integer.parseInt(txtHopCount.getText());
                timeout = Integer.parseInt(txtTimeoutSec.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Default search options will be used", "Invalid Search Options!",
                                              JOptionPane.ERROR_MESSAGE);
            }
        }

        Store store = Store.getInstance();
        String key = txtSearchKey.getText();
        Peer localPeer = store.getLocalPeer();
        SearchRequest searchRequest = new SearchRequest(Calendar.getInstance().getTimeInMillis(),
                                                        key, hopCount, localPeer);
        store.setMySearchRequest(searchRequest);
        store.addSearchRequest(searchRequest);
        store.setSearchResults(new ArrayList<>());
        List<DFile> results = Store.getInstance().findInFiles(searchRequest.getSearchKey());
        if (!results.isEmpty()) {
            SearchResult searchResult = new SearchResult(key, localPeer, 0, results);
            store.addSearchResult(searchResult);
        }
        for (Map.Entry<String, Peer> entry : Store.getInstance().getPeerMap().entrySet()) {
            Peer peer = entry.getValue();
            MessageUtils.sendUDPMessage(peer.getIp(),
                                        peer.getPort(),
                                        MessageType.SER + " " + localPeer.getIp() + " " + localPeer.getPort()
                                                + " \"" + key + "\" 2");
        }
        Runnable resultTask = () -> {
            try {
                List<SearchResult> searchResults = Store.getInstance().getSearchResults();
                resultFiles = new HashMap<>();
                if (searchResults != null) {
                    DefaultTableModel model = (DefaultTableModel) tblSearchResults.getModel();
                    model.setRowCount(0);
                    for (int i = 0; i < searchResults.size(); i++) {
                        Object[] data = new Object[MAX_COLS];
                        SearchResult searchResult = searchResults.get(i);
                        Peer peer = searchResult.getPeerWithResults();
                        data[0] = peer.getKey();
                        data[1] = searchResult.getHopCount();
                        for (DFile dFile : searchResult.getResults()) {
                            data[2] = dFile.getFileName();
                            data[3] = new StarRater(5, dFile.getTotalRating(), 0);
                            resultFiles.put(dFile.getFileName(), dFile);
                            model.addRow(data);
                        }
                    }
                    tblSearchResults.setModel(model);
                    model.fireTableDataChanged();
                }
            } catch (Throwable t) {
                System.out.println(t.getMessage());
            } finally {
                btnSearch.setEnabled(true);
            }
        };
        initSearchResultsTable();
        initCommentResultsTable();
        btnSearch.setEnabled(false);
        tblSearchResults.setVisible(true);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(resultTask, timeout, TimeUnit.SECONDS);
        resetAndStartProgress(timeout);
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnFilesListActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilesListActionPerformed
        new PeersListForm().setVisible(true);
    }//GEN-LAST:event_btnFilesListActionPerformed

    private void btnPeersListActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPeersListActionPerformed
        new MyFilesListForm().setVisible(true);
    }//GEN-LAST:event_btnPeersListActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        performExit();
    }//GEN-LAST:event_formWindowClosing

    private void chkAdvancedSearchActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAdvancedSearchActionPerformed
        if (!advancedSearchEnabled) {
            advancedSearchEnabled = true;
            setVisibleAdvancedSearchPanel(true);
        } else {
            advancedSearchEnabled = false;
            setVisibleAdvancedSearchPanel(false);
        }
    }//GEN-LAST:event_chkAdvancedSearchActionPerformed

    private void sliderHopCountStateChanged(
            javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderHopCountStateChanged
        txtHopCount.setText(String.valueOf(sliderHopCount.getValue()));
    }//GEN-LAST:event_sliderHopCountStateChanged

    private void sliderTimoutStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderTimoutStateChanged
        txtTimeoutSec.setText(String.valueOf(sliderTimout.getValue()));
    }//GEN-LAST:event_sliderTimoutStateChanged

    private void btnNewCommentActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewCommentActionPerformed
        int row = tblSearchResults.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a file first.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String fileName = tblSearchResults.getModel().getValueAt(row, 2).toString();
        String commentString = JOptionPane.showInputDialog(null, "Add comment for " + fileName, "Add A New Comment", JOptionPane.QUESTION_MESSAGE);
        if (commentString != null && !commentString.isEmpty()) {
            String username = Store.getInstance().getLocalPeer().getUsername();
            DFile commentedFile = resultFiles.get(fileName);
            Comment comment = new Comment();
            comment.setCommentId(RandomStringUtils.randomAlphanumeric(8));
            comment.setFileName(fileName);
            comment.setUserName(username);
            comment.setText(commentString);

            List<Comment> comments = commentedFile.getComments();
            comments.add(comment);

            Store.getInstance().addComment(comment);
            final String commentJSON = new Gson().toJson(comment);
            Store.getInstance().getPeerList().forEach(stringPeerEntry -> {
                String peerIP = stringPeerEntry.getValue().getIp();
                int peerPort = stringPeerEntry.getValue().getPort();
                Peer localPeer = Store.getInstance().getLocalPeer();
                MessageUtils.sendUDPMessage(peerIP,
                                            peerPort,
                                            MessageType.COMMENT + " " + localPeer.getIp() + " " +
                                            localPeer.getPort() + " " + commentJSON);
            });
        }
    }//GEN-LAST:event_btnNewCommentActionPerformed

    private void performExit() {
        Store store = Store.getInstance();
        Peer localPeer = store.getLocalPeer();
        for (Map.Entry<String, Peer> entry : Store.getInstance().getPeerMap().entrySet()) {
            Peer peer = entry.getValue();
            MessageUtils.sendUDPMessage(peer.getIp(),
                                        peer.getPort(),
                                        MessageType.LEAVE + " " + localPeer.getIp() + " " + localPeer.getPort());
        }
        MessageUtils.sendTCPMessage(store.getServerIp(),
                                    store.getServerPort(),
                                    MessageType.UNREG + " " + localPeer.getIp() + " " + localPeer.getPort() + " "
                                            + localPeer.getUsername());
    }

    private void resetAndStartProgress(int durationSec) {
        int maxValue = durationSec * 1000;
        progressBar.setMinimum(0);
        progressBar.setMaximum(maxValue);
        progressBar.setValue(0);
        progressBar.setVisible(true);

        new Thread(() -> {
            int count = 0;
            while (count < maxValue) {
                count = count + 100;
                progressBar.setValue(count);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    //do nothing
                }
            }
            progressBar.setVisible(false);
        }).start();
    }

    private void setVisibleAdvancedSearchPanel(boolean visibility) {
        lblHopCount.setVisible(visibility);
        txtHopCount.setVisible(visibility);
        lblTimeout.setVisible(visibility);
        txtTimeoutSec.setVisible(visibility);
        lblSec.setVisible(visibility);
        sliderHopCount.setVisible(visibility);
        sliderTimout.setVisible(visibility);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFilesList;
    private javax.swing.JButton btnNewComment;
    private javax.swing.JButton btnPeersList;
    private javax.swing.JButton btnSearch;
    private javax.swing.JCheckBox chkAdvancedSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblHopCount;
    private javax.swing.JLabel lblSec;
    private javax.swing.JLabel lblTimeout;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JSlider sliderHopCount;
    private javax.swing.JSlider sliderTimout;
    private javax.swing.JTable tblComments;
    private javax.swing.JTable tblSearchResults;
    private javax.swing.JTextArea txtCommentThread;
    private javax.swing.JTextField txtHopCount;
    private javax.swing.JTextField txtSearchKey;
    private javax.swing.JTextField txtTimeoutSec;
    // End of variables declaration//GEN-END:variables
}

class StarRatingsPanel extends JPanel {

    private static String DEFAULT = "0";
    protected volatile StarRater starRater = new StarRater(DashboardForm.MAX_RATING, 0, 0);

    public StarRatingsPanel() {
        setLayout(new GridLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        Store store = Store.getInstance();
        starRater.addStarListener(
                selection -> {
                    if (selection > DashboardForm.MAX_RATING) {
                        selection = DashboardForm.MAX_RATING;
                    }
                    Map<String, Object> properties = starRater.getProperties();
                    String fileName = null;
                    String commentId = null;
                    DashboardForm.StarRatingsType type = null;
                    if (properties != null && !properties.isEmpty()) {
                        fileName = (String) properties.get("fileName");
                        commentId = (String) properties.get("comment");
                        type = (DashboardForm.StarRatingsType) properties.get("type");
                    }

                    String username = Store.getInstance().getLocalPeer().getUsername();
                    DFile ratedFile = resultFiles.get(fileName);
                    Rating rating = new Rating();
                    rating.setFileName(fileName);
                    rating.setCommentId(commentId);
                    rating.setRatingId(RandomStringUtils.randomAlphanumeric(8));
                    rating.setUserName(username);
                    rating.setValue(selection);

                    if (DashboardForm.StarRatingsType.COMMENT == type) {
                        List<Comment> comments = ratedFile.getComments();
                        Comment comment = null;
                        for (Comment c : comments) {
                            if (c.getCommentId().equals(commentId)) {
                                comment = c;
                                break;
                            }
                        }
                        if (comment != null) {
                            List<Rating> commentRatings = comment.getRatings();
                            boolean isNotRated = true;
                            for (Rating r : commentRatings) {
                                if (r.getUserName().equals(username)) {
                                    r.setValue(selection);
                                    isNotRated = false;
                                }
                            }
                            if (isNotRated) {
                                commentRatings.add(rating);
                            }
                        }
                    } else {
                        List<Rating> fileRatings = ratedFile.getRatings();
                        boolean isNotRated = true;
                        for (Rating r : fileRatings) {
                            if (r.getUserName().equals(username)) {
                                r.setValue(selection);
                                isNotRated = false;
                            }
                        }
                        if (isNotRated) {
                            fileRatings.add(rating);
                        }
                    }

                    Store.getInstance().addRating(rating);
                    final String ratingJSON = new Gson().toJson(rating);
                    store.getPeerList().forEach(stringPeerEntry -> {
                        String peerIP = stringPeerEntry.getValue().getIp();
                        int peerPort = stringPeerEntry.getValue().getPort();
                        Peer localPeer = store.getLocalPeer();
                        MessageUtils.sendUDPMessage(peerIP,
                                                    peerPort,
                                                    MessageType.RATE + " " + localPeer.getIp() + " " +
                                                            localPeer.getPort() + " " + ratingJSON);
                    });
                });
        add(starRater);
    }

    public void updateValue(StarRater bt, String fileName, String comment, DashboardForm.StarRatingsType type) {
        starRater.setRating(bt.getRating());
        Map<String, Object> properties = new HashMap<>();
        properties.put("fileName", fileName);
        properties.put("comment", comment);
        properties.put("type", type);
        starRater.setProperties(properties);
    }
}

class ButtonPanel extends JPanel {

    private static String DEFAULT = "0";
    protected JTable jTable;
    protected volatile JButton button = new JButton("Reply");

    public ButtonPanel(JTable jTable) {
        this.jTable = jTable;
        setLayout(new GridLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        button.addActionListener(e -> {
            int row = jTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a comment first.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String commentId = jTable.getValueAt(jTable.getSelectedRow(), 0).toString();
            String commentString = JOptionPane.showInputDialog(null, "Add reply for " + commentId, "Add A New Reply", JOptionPane.QUESTION_MESSAGE);
            if (commentString != null && !commentString.isEmpty()) {
                String username = Store.getInstance().getLocalPeer().getUsername();
                DFile commentedFile = resultFiles.get(selectedFile);
                List<Comment> comments = commentedFile.getComments();
                Comment selectedComment = null;
                for (Comment c : comments) {
                    if (c.getCommentId().equals(commentId)){
                        selectedComment = c;
                        break;
                    }
                }

                if (selectedComment != null) {
                    Comment replyComment = new Comment();
                    replyComment.setCommentId(RandomStringUtils.randomAlphanumeric(8));
                    replyComment.setFileName(selectedFile);
                    replyComment.setParentId(commentId);
                    replyComment.setUserName(username);
                    replyComment.setText(commentString);

                    selectedComment.getReplies().add(replyComment);
                    Store.getInstance().addComment(replyComment);
                    final String commentJSON = new Gson().toJson(replyComment);
                    Store.getInstance().getPeerList().forEach(stringPeerEntry -> {
                        String peerIP = stringPeerEntry.getValue().getIp();
                        int peerPort = stringPeerEntry.getValue().getPort();
                        Peer localPeer = Store.getInstance().getLocalPeer();
                        MessageUtils.sendUDPMessage(peerIP,
                                                    peerPort,
                                                    MessageType.COMMENT + " " + localPeer.getIp() + " " +
                                                    localPeer.getPort() + " " + commentJSON);
                    });
                }
            }
        });
        add(button);
    }
}

class ButtonRenderer extends ButtonPanel implements TableCellRenderer {

    public ButtonRenderer(JTable jTable) {
        super(jTable);
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return this;
    }
}

class ButtonEditor extends ButtonPanel implements TableCellEditor {
    protected transient ChangeEvent changeEvent;

    public ButtonEditor(JTable jTable) {
        super(jTable);
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.setBackground(table.getSelectionBackground());
        return this;
    }

    public Object getCellEditorValue() {
        return button;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }

    @Override
    public void cancelCellEditing() {
        fireEditingCanceled();
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        listenerList.add(CellEditorListener.class, l);
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        listenerList.remove(CellEditorListener.class, l);
    }

    public CellEditorListener[] getCellEditorListeners() {
        return listenerList.getListeners(CellEditorListener.class);
    }

    protected void fireEditingStopped() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CellEditorListener.class) {
                // Lazily create the event:
                if (Objects.isNull(changeEvent)) {
                    changeEvent = new ChangeEvent(this);
                }
                ((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
            }
        }
    }

    protected void fireEditingCanceled() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CellEditorListener.class) {
                // Lazily create the event:
                if (Objects.isNull(changeEvent)) {
                    changeEvent = new ChangeEvent(this);
                }
                ((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
            }
        }
    }
}

class StarRatingsRenderer extends StarRatingsPanel implements TableCellRenderer {

    public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
    public static final int TYPE_COMMENT = 1;
    public static final int TYPE_FILE = 2;

    private final JTable jTable;
    private final DashboardForm.StarRatingsType type;

    public StarRatingsRenderer() {
        super();
        setName("Table.cellRenderer");
        this.jTable = null;
        this.type = DashboardForm.StarRatingsType.DEFAULT;
    }

    public StarRatingsRenderer(JTable jTable, DashboardForm.StarRatingsType type) {
        this.jTable = jTable;
        this.type = type;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        if (jTable != null) {
            String fileName;
            String comment = "";
            if (type == DashboardForm.StarRatingsType.COMMENT) {
                comment = (String) jTable.getModel().getValueAt(row, DashboardForm.COMMENT_COL_INDEX);
                fileName = selectedFile;
            } else {
                fileName = (String) jTable.getModel().getValueAt(row, DashboardForm.FILE_COL_INDEX);
            }
            updateValue((StarRater) value, fileName, comment, type);
        }
        return this;
    }
}

class StarRatingsEditor extends StarRatingsPanel implements TableCellEditor {

    private final JTable jTable;
    private final DashboardForm.StarRatingsType type;
    protected transient ChangeEvent changeEvent;

    public StarRatingsEditor(JTable jTable, DashboardForm.StarRatingsType type) {
        this.jTable = jTable;
        this.type = type;
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {
        this.setBackground(table.getSelectionBackground());
        String fileName;
        String comment = "";
        if (type == DashboardForm.StarRatingsType.COMMENT) {
            comment = (String) jTable.getModel().getValueAt(row, DashboardForm.COMMENT_COL_INDEX);
            fileName = selectedFile;
        } else {
            fileName = (String) jTable.getModel().getValueAt(row, DashboardForm.FILE_COL_INDEX);
        }
        jTable.getModel().setValueAt(value, row, DashboardForm.STAR_RATINGS_COL_INDEX);
        updateValue((StarRater) value, fileName, comment, type);
        return this;
    }

    @Override
    public Object getCellEditorValue() {
        return starRater;
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }

    @Override
    public void cancelCellEditing() {
        fireEditingCanceled();
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        listenerList.add(CellEditorListener.class, l);
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        listenerList.remove(CellEditorListener.class, l);
    }

    public CellEditorListener[] getCellEditorListeners() {
        return listenerList.getListeners(CellEditorListener.class);
    }

    protected void fireEditingStopped() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CellEditorListener.class) {
                // Lazily create the event:
                if (Objects.isNull(changeEvent)) {
                    changeEvent = new ChangeEvent(this);
                }
                ((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
            }
        }
    }

    protected void fireEditingCanceled() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CellEditorListener.class) {
                // Lazily create the event:
                if (Objects.isNull(changeEvent)) {
                    changeEvent = new ChangeEvent(this);
                }
                ((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
            }
        }
    }
}

   public static class Mayank_ViewMarksFrame extends JFrame implements ActionListener {
      JTextField tfFilter;
      JTextArea ta;
      JButton btnRefresh;
      JButton btnBack;
      JButton btnFilter;
      String user;
      String role;

      public Mayank_ViewMarksFrame(String var1, String var2) {
         super("View Students & Marks (Mayank)");
         this.user = var1;
         this.role = var2;
         this.tfFilter = new JTextField(20);
         this.ta = new JTextArea(20, 70);
         this.ta.setFont(new Font("Monospaced", 0, 12));
         this.btnRefresh = new JButton("Refresh");
         this.btnFilter = new JButton("Filter (roll/class)");
         this.btnBack = new JButton("Back");
         this.btnRefresh.addActionListener(this);
         this.btnFilter.addActionListener(this);
         this.btnBack.addActionListener((var3x) -> {
            this.dispose();
            new Yachika_DashboardFrame(var1, var2);
         });
         JPanel var3 = new JPanel();
         var3.add(new JLabel("Filter:"));
         var3.add(this.tfFilter);
         var3.add(this.btnFilter);
         var3.add(this.btnRefresh);
         var3.add(this.btnBack);
         this.add(var3, "North");
         this.add(new JScrollPane(this.ta), "Center");
         this.setSize(900, 600);
         this.setLocationRelativeTo((Component)null);
         this.setVisible(true);
         this.loadAll("");
      }

      public void actionPerformed(ActionEvent var1) {
         String var2 = var1.getActionCommand();
         if (var2.equals("Refresh")) {
            this.loadAll("");
         } else if (var2.equals("Filter (roll/class)")) {
            this.loadAll(this.tfFilter.getText().trim());
         }

      }

      void loadAll(String var1) {
         this.ta.setText("");

         try {
            Connection var2 = MarksManagement.DB.getConnection();

            try {
               ArrayList var3 = new ArrayList();
               Statement var4 = var2.createStatement();

               try {
                  ResultSet var5 = var4.executeQuery("SELECT name FROM subjects ORDER BY name");

                  try {
                     while(var5.next()) {
                        var3.add(var5.getString(1));
                     }
                  } catch (Throwable var29) {
                     if (var5 != null) {
                        try {
                           var5.close();
                        } catch (Throwable var24) {
                           var29.addSuppressed(var24);
                        }
                     }

                     throw var29;
                  }

                  if (var5 != null) {
                     var5.close();
                  }
               } catch (Throwable var30) {
                  if (var4 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var23) {
                        var30.addSuppressed(var23);
                     }
                  }

                  throw var30;
               }

               if (var4 != null) {
                  var4.close();
               }

               StringBuilder var33 = new StringBuilder();
               var33.append(String.format("%-6s %-25s %-10s", "Roll", "Name", "Class"));
               Iterator var34 = var3.iterator();

               while(var34.hasNext()) {
                  String var6 = (String)var34.next();
                  var33.append(String.format(" %-10s", var6.length() > 9 ? var6.substring(0, 9) : var6));
               }

               var33.append("\n");
               this.ta.append(var33.toString());
               this.ta.append("-".repeat(Math.max(0, var33.length())));
               this.ta.append("\n");
               String var35 = "SELECT roll,name,class FROM students";
               if (!var1.isBlank()) {
                  var35 = var35 + " WHERE roll LIKE ? OR class LIKE ?";
               }

               PreparedStatement var36 = var2.prepareStatement(var35);

               try {
                  if (!var1.isBlank()) {
                     var36.setString(1, "%" + var1 + "%");
                     var36.setString(2, "%" + var1 + "%");
                  }

                  ResultSet var7 = var36.executeQuery();

                  try {
                     while(var7.next()) {
                        String var8 = var7.getString("roll");
                        String var9 = var7.getString("name");
                        String var10 = var7.getString("class");
                        StringBuilder var11 = new StringBuilder();
                        var11.append(String.format("%-6s %-25s %-10s", var8, var9, var10));
                        Iterator var12 = var3.iterator();

                        while(var12.hasNext()) {
                           String var13 = (String)var12.next();
                           PreparedStatement var14 = var2.prepareStatement("SELECT marks FROM marks WHERE roll=? AND subject=?");

                           try {
                              var14.setString(1, var8);
                              var14.setString(2, var13);
                              ResultSet var15 = var14.executeQuery();

                              try {
                                 if (var15.next()) {
                                    int var16 = var15.getInt(1);
                                    String var17 = var16 < 0 ? "-" : String.valueOf(var16);
                                    var11.append(String.format(" %-10s", var17));
                                 } else {
                                    var11.append(String.format(" %-10s", "-"));
                                 }
                              } catch (Throwable var25) {
                                 if (var15 != null) {
                                    try {
                                       var15.close();
                                    } catch (Throwable var22) {
                                       var25.addSuppressed(var22);
                                    }
                                 }

                                 throw var25;
                              }

                              if (var15 != null) {
                                 var15.close();
                              }
                           } catch (Throwable var26) {
                              if (var14 != null) {
                                 try {
                                    var14.close();
                                 } catch (Throwable var21) {
                                    var26.addSuppressed(var21);
                                 }
                              }

                              throw var26;
                           }

                           if (var14 != null) {
                              var14.close();
                           }
                        }

                        this.ta.append(var11.toString());
                        this.ta.append("\n");
                     }
                  } catch (Throwable var27) {
                     if (var7 != null) {
                        try {
                           var7.close();
                        } catch (Throwable var20) {
                           var27.addSuppressed(var20);
                        }
                     }

                     throw var27;
                  }

                  if (var7 != null) {
                     var7.close();
                  }
               } catch (Throwable var28) {
                  if (var36 != null) {
                     try {
                        var36.close();
                     } catch (Throwable var19) {
                        var28.addSuppressed(var19);
                     }
                  }

                  throw var28;
               }

               if (var36 != null) {
                  var36.close();
               }
            } catch (Throwable var31) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var18) {
                     var31.addSuppressed(var18);
                  }
               }

               throw var31;
            }

            if (var2 != null) {
               var2.close();
            }
         } catch (Exception var32) {
            this.ta.setText("DB Error: " + var32.getMessage());
         }

      }
   }
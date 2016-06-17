package timesheet.panels;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.GridBagLayout;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import timesheet.Application;
import timesheet.RDNE;
import timesheet.DTO.DTOProject;
import timesheet.DTO.DTOProjectTimeSheet;
import timesheet.DTO.DTOTime;
import timesheet.DTO.ViewProjectTimesheet;
import timesheet.connection.DBEngine.DbEngine;

public class TimesheetView extends JPanel {
	private static final long serialVersionUID = 1L;

	private List<Row> rows = new ArrayList<>();

	JLabel lblDay1;
	JLabel lblDay2;
	JLabel lblDay3;
	JLabel lblDay4;
	JLabel lblDay5;
	JLabel lblDay6;
	JLabel lblDay7;
	JLabel lblTotal;
	JTextField txtTot1;
	JTextField txtTot2;
	JTextField txtTot3;
	JTextField txtTot4;
	JTextField txtTot5;
	JTextField txtTot6;
	JTextField txtTot7;

	List<JTextField> txtEndOfRows = new ArrayList<>();
	private JLabel lblNewLabel;

	private JLabel[] getLables() {
		JLabel[] lables = { lblDay1, lblDay2, lblDay3, lblDay4, lblDay5, lblDay6, lblDay7 };
		return lables;
	}

	private JTextField[] getTotalBoxes() {
		JTextField[] lables = { txtTot1, txtTot2, txtTot3, txtTot4, txtTot5, txtTot6, txtTot7 };
		return lables;
	}

	/**
	 * Create the panel.
	 * 
	 * @throws SQLException
	 * @throws RDNE
	 */
	public TimesheetView() throws SQLException, RDNE {

		jbinit();
		labelTextInit();
		createTextFields();
		populateTextField();
		calculateTotals();
		repaint();
		validate();
	}

	private void calculateTotals() {

		List<Row> rows2 = getRows();
		if (rows2.isEmpty())
			return;
		double time[][] = new double[rows2.size()][7];
		for (int i = 0; i < getRows().size(); i++) {
			Row row = getRows().get(i);
			for (int j = 0; j < row.getTxtRowDay().size(); j++) {
				JTextField jTextField = row.getTxtRowDay().get(j);
				double timeLogged = Double.valueOf(jTextField.getText());
				time[i][j] = timeLogged;
			}
		}

		// gets the total for the end of the rows
		int it = 0;
		{
			double totalRowTime[] = new double[getRows().size()];
			for (double[] x : time) {
				double tmp = 0.0;
				for (double y : x) {
					tmp += y;
				}
				totalRowTime[it++] = tmp;
			}
			for (int k = 0; k < txtEndOfRows.size(); k++) {
				JTextField jTextField = txtEndOfRows.get(k);
				jTextField.setText(String.valueOf(totalRowTime[k]));
			}
		}
		{
			// gets the column totals
			double totalTime[] = columnSum(time);

			// sets totals at bottom
			JTextField[] totalBoxes = getTotalBoxes();
			for (int j = 0; j < totalBoxes.length; j++) {
				double d = totalTime[j];
				if (d < Application.hoursInDay) {
					totalBoxes[j].setBackground(Color.RED);
				} else if (d == Application.hoursInDay) {
					totalBoxes[j].setBackground(Color.YELLOW);
				} else if (d > Application.hoursInDay) {
					totalBoxes[j].setBackground(Color.GREEN);
				}
				totalBoxes[j].setText(String.valueOf(d));
			}
		}

	}

	private double[] columnSum(double[][] array) {
		int size = array[0].length;
		double temp[] = new double[size];

		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				temp[j] += array[i][j];
			}
		}
		return temp;
	}

	public void repopulateTextFields() throws SQLException, RDNE {
		rows = new ArrayList<>();
		lblTotal = null;
		createTextFields();
		populateTextField();
		calculateTotals();
		repaint();
		validate();
	}

	public void populateTextField() throws SQLException, RDNE {
		DbEngine db = new DbEngine();
		for (Row row : getRows()) {

			ViewProjectTimesheet loggedTimeByResourceByPojectTS = db
					.getLoggedTimeByResourceByPojectTS(Application.resource, row.getProjectTimesheet());

			List<DTOTime> datesToLookAt = loggedTimeByResourceByPojectTS.getTime();

			List<JTextField> txtRowDay = row.getTxtRowDay();
			DateTime firstDayOfWeek = row.getDates();
			for (JTextField jTextField : txtRowDay) {
				for (DTOTime dtoTime : datesToLookAt) {
					if (firstDayOfWeek.withTimeAtStartOfDay().equals(dtoTime.getDate().withTimeAtStartOfDay())) {
						String valueOf = String.valueOf(dtoTime.getLogged());
						if (valueOf == null || valueOf.equals(""))
							valueOf = "0.0";
						jTextField.setText(valueOf);
					}
				}
				firstDayOfWeek = firstDayOfWeek.plusDays(1);
			}
		}

	}

	private void createTextFields() throws SQLException, RDNE {
		DbEngine db = new DbEngine();

		List<DTOProjectTimeSheet> allProjectsTimeSheetForResource = db
				.getAllProjectsTimeSheetForResource(Application.resource);
		int y = 1;
		for (DTOProjectTimeSheet dtoProjectTimeSheet : allProjectsTimeSheetForResource) {
			int x = 2;
			List<JTextField> txt = new ArrayList<>();
			for (int i = 0; i < 7; i++) {
				NumberFormat format = NumberFormat.getInstance();
			    NumberFormatter formatter = new NumberFormatter(format);
			    formatter.setMinimum(0.0);
			    formatter.setMaximum(24.0);
			    formatter.setAllowsInvalid(false);
			    formatter.setCommitsOnValidEdit(true);
				JFormattedTextField txtField = new JFormattedTextField(formatter);
				txtField.setSize(new Dimension(20, 20));
				GridBagConstraints gbc_textField = new GridBagConstraints();
				gbc_textField.insets = new Insets(0, 0, 5, 5);
				gbc_textField.fill = GridBagConstraints.HORIZONTAL;
				gbc_textField.gridx = x++;
				gbc_textField.gridy = y;
				add(txtField, gbc_textField);
				txtField.setColumns(10);
				txtField.setText("0.0");
				txtField.setValue(new Double(0.0));
				txtField.addFocusListener(getFocus());
		
				txt.add(txtField);
			}

			JTextField endOfRow = new JTextField();
			endOfRow.setSize(new Dimension(20, 20));
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.insets = new Insets(0, 0, 5, 5);
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.gridx = x;
			gbc_textField.gridy = y;
			add(endOfRow, gbc_textField);
			endOfRow.setColumns(10);
			endOfRow.setText("0.0");
			endOfRow.setEditable(false);
			txtEndOfRows.add(endOfRow);

			DTOProject project = db.getProject(dtoProjectTimeSheet.getProjectId());
			JLabel lblProject = new JLabel(project.getProjectName());
			GridBagConstraints gbc_lblProject = new GridBagConstraints();
			gbc_lblProject.insets = new Insets(0, 0, 5, 5);
			gbc_lblProject.anchor = GridBagConstraints.EAST;
			gbc_lblProject.gridx = 1;
			gbc_lblProject.gridy = y;
			add(lblProject, gbc_lblProject);

			y++;

			rows.add(new Row(lblProject, txt, dtoProjectTimeSheet, getFirstDateOfWeek()));
		}

		// Create total rows
		int x2 = 2;
		for (JTextField jTextField : getTotalBoxes()) {
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.insets = new Insets(0, 0, 5, 5);
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.gridx = x2++;
			gbc_textField.gridy = y;
			add(jTextField, gbc_textField);
			jTextField.setEditable(false);
			jTextField.setColumns(10);
		}

		lblNewLabel = new JLabel("Total");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 9;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);

		lblTotal = new JLabel("Total");
		GridBagConstraints gbg = new GridBagConstraints();
		gbg.anchor = GridBagConstraints.EAST;
		gbg.insets = new Insets(0, 0, 5, 5);
		gbg.gridx = 1;
		gbg.gridy = y;
		add(lblTotal, gbg);
	}

	private FocusListener getFocus() {
		return new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				calculateTotals();
			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub

			}
		};
	}

	private void labelTextInit() {
		DateTime first = getFirstDateOfWeek();
		DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE / dd");
		for (Component component : getLables()) {
			((JLabel) component).setText(fmt.print(first));
			first = first.plusDays(1);
		}
	}

	private DateTime getFirstDateOfWeek() {
		DateTime dateTime = new DateTime();
		long firstDayOfWeekTimestamp = dateTime.withDayOfWeek(1).getMillis();
		DateTime first = new DateTime(firstDayOfWeekTimestamp);
		return first;
	}

	private void jbinit() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0,
				Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		lblDay1 = new JLabel();
		lblDay1.setText(".");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 0;
		add(lblDay1, gbc_lblNewLabel);

		lblDay2 = new JLabel(".");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 3;
		gbc_lblNewLabel_1.gridy = 0;
		add(lblDay2, gbc_lblNewLabel_1);

		lblDay3 = new JLabel(".");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 4;
		gbc_lblNewLabel_2.gridy = 0;
		add(lblDay3, gbc_lblNewLabel_2);

		lblDay4 = new JLabel(".");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 5;
		gbc_lblNewLabel_3.gridy = 0;
		add(lblDay4, gbc_lblNewLabel_3);

		lblDay5 = new JLabel(".");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 6;
		gbc_lblNewLabel_4.gridy = 0;
		add(lblDay5, gbc_lblNewLabel_4);

		lblDay6 = new JLabel(".");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_5.gridx = 7;
		gbc_lblNewLabel_5.gridy = 0;
		add(lblDay6, gbc_lblNewLabel_5);

		lblDay7 = new JLabel(".");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_6.gridx = 8;
		gbc_lblNewLabel_6.gridy = 0;
		add(lblDay7, gbc_lblNewLabel_6);

		txtTot1 = new JTextField();
		txtTot2 = new JTextField();
		txtTot3 = new JTextField();
		txtTot4 = new JTextField();
		txtTot5 = new JTextField();
		txtTot6 = new JTextField();
		txtTot7 = new JTextField();
	}

	public List<Row> getRows() {
		return rows;
	}
}

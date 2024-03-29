package fxmlControllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import UtilitiesFx.graphicalTools.MousePressed;
import UtilitiesFx.graphicalTools.Tools;
import dataLoader.MaskRestrictionDataLoader;
import eu.hansolo.fx.charts.CircularPlot;
import eu.hansolo.fx.charts.CircularPlotBuilder;
import eu.hansolo.fx.charts.data.PlotItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import model.CellsSet;

public class MasksPaneController {
	@FXML
	private VBox boxMaskTypes;
	@FXML
	ScrollPane scroll;
	MaskRestrictionDataLoader Maskloader = new MaskRestrictionDataLoader();
	ArrayList<CheckBox> radioListOfMasks = new ArrayList<>();
	// cell.getMaskTyp->hash(owner_competitor-> true or false)
	public static HashMap<String, HashMap<String, Boolean>> restrictions = new HashMap<>();
	CircularPlot[] circularPlot;

	@SuppressWarnings("unchecked")
	public void initialize() {
		scroll.setPrefHeight(Screen.getPrimary().getBounds().getHeight() * .9);
		Maskloader.MaskAndRistrictionLaoder();
		MaskRestrictionDataLoader.ListOfMask.keySet().forEach(n -> {
			CheckBox r = new CheckBox(n);
			radioListOfMasks.add(r);
			boxMaskTypes.getChildren().add(r);
		});
		circularPlot = new CircularPlot[radioListOfMasks.size()];
		TitledPane[] T = new TitledPane[radioListOfMasks.size()];
		radioListOfMasks.forEach(r -> {
			r.setOnAction(e -> {
				int i = radioListOfMasks.indexOf(r);
				if (r.isSelected()) {
					ArrayList<CheckBox> radioListOfAFTs = new ArrayList<>();
					VBox boxOfAftRadios = new VBox();
					TabPaneController.M.AFtsSet.getAftHash().keySet().forEach(n -> {
						CheckBox radio = new CheckBox(n);
						radioListOfAFTs.add(radio);
						boxOfAftRadios.getChildren().add(radio);
					});

					Maskloader.CellSetToMaskLoader(r.getText());
					HashMap<String, Boolean> restrictionsRul = Maskloader.restrictionsRulsUpload(r.getText());
					restrictions.put(r.getText(), restrictionsRul);
					ArrayList<PlotItem> itemsList = initPlotItem();
					radioListOfAFTs.get(0).setSelected(true);
					List<PlotItem> items = circularPlot(itemsList, restrictionsRul, radioListOfAFTs.get(0).getText(),true);
					circularPlot[i] = CircularPlotBuilder.create().items(items).decimals(0).minorTickMarksVisible(false)
							.build();
					VBox boxMask = new VBox();
					T[i] = Tools.T("  Possible transitions for " + r.getText() + " Restriction ", true, boxMask);
					Text text = Tools.text("Select the AFT (landowner) to display the possible transitions from this AFT to other AFTs (competitors):", Color.BLUE);
					boxMask.getChildren().addAll(Tools.hBox(text),
							Tools.hBox(boxOfAftRadios, circularPlot[i]));
//		
					radioListOfAFTs.forEach(rad -> {
						rad.setOnAction(e2 -> {
								circularPlot[i].setItems(circularPlot(itemsList, restrictionsRul, rad.getText(),rad.isSelected()));
							
						});
					});

					MousePressed.mouseControle(boxMask, circularPlot[i]);
					int place = boxMaskTypes.getChildren().indexOf(r) + 1;
					boxMaskTypes.getChildren().add(place, T[i]);
				} else {
					CellsSet.getCells().forEach(c -> {
						if (c.getMaskType() != null && c.getMaskType().equals(r.getText()))
							c.setMaskType(null);
					});
					restrictions.remove(r.getText());
					boxMaskTypes.getChildren().removeAll(T[i]);

				}
				CellsSet.colorMap("Mask");
				System.out.println(restrictions.keySet());
			});

		});

	}

	// Event Listener on Button[#handButton].onAction
	@FXML
	public void clear(ActionEvent event) {
//		CellsSet.getCellsSet().forEach(c -> {
//			c.setMaskType(null);
//		});
		CellsSet.colorMap("Mask");
		radioListOfMasks.forEach(r -> {
			r.setSelected(false);
			r.fireEvent(event);
		});
	}

	private ArrayList<PlotItem> initPlotItem() {
		ArrayList<PlotItem> itemsList = new ArrayList<>();
		TabPaneController.M.AFtsSet.forEach(a -> {
			itemsList.add(new PlotItem(a.getLabel(), 10, a.getColor()));
		});
		return itemsList;
	}

	private List<PlotItem> circularPlot(ArrayList<PlotItem> itemsList, HashMap<String, Boolean> restrictions,
			String ow, boolean toAdd) {

		// itemsList.forEach(owner -> {});
		
		PlotItem own = null;
		for (Iterator<PlotItem> iterator = itemsList.iterator(); iterator.hasNext();) {
			PlotItem plotItem = (PlotItem) iterator.next();
			if (plotItem.getName().equals(ow)) {
				own = plotItem;
				break;
			}
		}
		PlotItem owner = own;
		itemsList.forEach(competitor -> {
			int nbr = restrictions.get(owner.getName() + "_" + competitor.getName()) ? 1 : -1;
			if(toAdd) {owner.addToOutgoing(competitor, nbr);}
			else owner.removeFromOutgoing(competitor);
		});

		PlotItem[] its = new PlotItem[itemsList.size()];
		for (int i = 0; i < its.length; i++) {
			its[i] = itemsList.get(i);
		}
		
		List<PlotItem> items = List.of(its);
		
		return items;
	}

}

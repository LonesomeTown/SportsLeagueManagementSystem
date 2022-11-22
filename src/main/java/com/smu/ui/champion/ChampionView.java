package com.smu.ui.champion;

import com.smu.dto.ChampionVo;
import com.smu.dto.League;
import com.smu.dto.LeagueVo;
import com.smu.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;

/**
 * Champion
 *
 * @author T.W 11/21/22
 */
@Route(value = "champion", layout = MainLayout.class)
@PageTitle("Champion | Project Group8")
public class ChampionView extends VerticalLayout {
    Grid<ChampionVo> grid = new Grid<>(ChampionVo.class, false);
    ComboBox<String> comboBox = new ComboBox<>();

    public ChampionView() {
        addClassName("champion-view");
    }

    private void configureGrid() {
        grid.addClassNames("champion-grid");
        grid.setSizeFull();
        grid.addColumn(ChampionVo::getTeamName).setHeader("Champion Team Name");
        grid.addColumn(ChampionVo::getSeasonDuration).setHeader("Season Duration");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.addComponentColumn(t -> createInlineButtonComponent(t.getSeasonId(),t.getTeamName()));
//        this.updateList();
    }

    private Button createInlineButtonComponent(ObjectId seasonId, String teamName) {
        Button tertiaryInlineButton = new Button("Records");
        tertiaryInlineButton
                .addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
//        tertiaryInlineButton.addClickListener(e -> configureDialog(seasonId));
        return tertiaryInlineButton;
    }
}

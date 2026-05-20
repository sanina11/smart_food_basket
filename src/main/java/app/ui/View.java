/*
 * Smart Food Basket - ODS Edition
 * Autor: Miguel Sanina - 26874, João Soares - 25961
 * Programação Orientada por Objetos - IPBeja 2025/2026
 *
 * Contém código gerado/refinado com auxílio de IA generativa (Claude).
 * Detalhes no ficheiro relatorio_ia.txt.
 */
package app.ui;

/**
 * Contract between the BasketModel and any user interface that wants to
 * display the basket state. The model notifies the view through this
 * interface whenever the basket changes, without depending on a specific
 * UI implementation.
 */
public interface View {

    /**
     * Refreshes the user interface to reflect the current state of the model.
     * Called by the model whenever items are added, removed, or modified.
     */
    void updateView();

    /**
     * Displays an error message to the user.
     *
     * @param message the description of the error to show
     */
    void showError(String message);
}



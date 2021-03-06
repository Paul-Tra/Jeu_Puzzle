package sample;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.awt.*;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Dents extends Forme_Bordure {
    private final double ANGLE_MAX = 25.0;
    private final double ANGLE_MIN = 20.0;
    private static final int DEFAULT_COTE = 0;
    private final static Boolean est_plat = false;
    private static final int DEFAULT_NIVEAU = 1;
    private static final double DEFAULT_DECALAGE = 0; // double pcq coef
    private static final double DEFAULT_MARGE_HAUTEUR = 0.10;
    // eviter la supperposition de deux creux sur deux cote contigu d'une piece
    private static final double DEFAULT_MARGE_HAUTEUR_CONTROLE = 0.05; // meme chose qu'au dessus mais ne sert uniquement au premier et derner cercle de controle d'une bordure
    // ceux qui snt de bas en hauteur 0.0 // evite le croisement des lignes entre divers cotee contigu d'une piece

    // matrices de positionnement des cercles
    private static double tab_coef_longeur[] = {0.4, 0.36, 0.50, 0.64, 0.60}; // cercle de l'appendice // 7-2 = 5
    private static double tab_coef_hauteur[] = {0.08, 0.26, 0.40, 0.26, 0.08};
    private static double tab_coef_longeur_controle[] = {0.25, 0.34, 0.43, 0.36, 0.36, 0.42, 0.58, 0.64, 0.64, 0.57, 0.66, 0.75};// tout les cercles de controles //12
    private static double tab_coef_hauteur_controle[] = {0.0, 0.0, 0.12, 0.14, 0.34, 0.40, 0.40, 0.34, 0.16, 0.12, 0.0, 0.0};

    private double tab_coef_longueur_approx[];
    private double tab_coef_hauteur_approx[];
    private double tab_coef_longueur_controle_approx[];
    private double tab_coef_hauteur_controle_approx[];

    Shape c;
    Random rand;
    int tmp;
    double posX = DEFAULT_COORD_X;
    double posY = DEFAULT_COORD_Y;
    private int cote = DEFAULT_COTE;
    private int niveau = DEFAULT_NIVEAU;

    private static final double MARGE_DECALAGE = 0.05;
    private double MARGE_HAUTEUR = DEFAULT_MARGE_HAUTEUR; // marge sur la hauteur d'un appendice
    private double MARGE_HAUTEUR_CONTROLE = DEFAULT_MARGE_HAUTEUR_CONTROLE; // meme chose qu'au dessus mais ne sert uniquement au premier et derner cercle de controle d'une bordure

    private double decalage = DEFAULT_DECALAGE;
    private boolean est_decalable = false;
    private boolean est_deformable_localement = false;
    private double min_taille ; //= Math.min(TAILLE_COTE_PIECE_HAUTEUR, TAILLE_COTE_PIECE_LONGUEUR);
    private boolean est_dernier = false; // sert pour deformation de cadre si tte fois on est sur la derniere ligne du plateau
    private boolean est_approximable = false; // sert pour la derniere deformation, l'approximation des points d'une bordure


    public Dents(int cote, double x, double y, double hauteur, double longueur, int niveau , double angle2) {
        super(est_plat);
        //Main.consumer.accept("dans Dents avec niveau");
        posY = y;
        posX = x;
        this.cote = cote;
        this.niveau = niveau;
        check_dernier();
        this.setAngle2(angle2);
        setTailleCotePieceHauteur(hauteur);
        setTailleCotePieceLongueur(longueur);
        init_MinTaille(hauteur,longueur);
        gestion_niveau();
        gestion_dimension_bordure();
        fill_liste_cercle();
        fill_list_cercle_controle();
        ajout_decalage();
        ajout_deformation_cadre_locale();
    }


    // determine si la bordure se trouve a la derniere ligne du plateau
    private void check_dernier() {
        if (this.cote == Piece.DROITE) {
            double c = this.posY / Plateau.getNb_ligne();
            if (c == TAILLE_COTE_PIECE_HAUTEUR) {
                this.est_dernier = true;
            }
        }
    }

    public Dents(int cote, double x, double y, double hauteur, double longueur, int niveau ,double angle1, double angle2) {
        super(est_plat);
        posY = y;
        posX = x;
        this.cote = cote;
        this.niveau = niveau;
        this.setAngle1(angle1);
        this.setAngle2(angle2);
        setTailleCotePieceHauteur(hauteur);
        setTailleCotePieceLongueur(longueur);
        init_MinTaille(hauteur,longueur);
        gestion_niveau();
        gestion_dimension_bordure();
        fill_liste_cercle();
        fill_list_cercle_controle();
        ajout_decalage();
        ajout_deformation_cadre_locale();
    }



    public Dents(int cote, double x, double y, double hauteur, double longueur) {
        super(est_plat);
        posY = y;
        posX = x;
        this.cote = cote;
        setTailleCotePieceHauteur(hauteur);
        setTailleCotePieceLongueur(longueur);
        init_MinTaille(hauteur,longueur);
        gestion_dimension_bordure();
        fill_liste_cercle();
        fill_list_cercle_controle();
        ajout_decalage();
        ajout_deformation_cadre_locale();
    }
    public Dents(int cote, double x, double y, double hauteur, double longueur, int niveau, boolean dernier) {
        super(est_plat);
        //Main.consumer.accept("dans Dents avec niveau");
        posY = y;
        posX = x;
        this.cote = cote;
        this.niveau = niveau;
        this.est_dernier = dernier;
        setTailleCotePieceHauteur(hauteur);
        setTailleCotePieceLongueur(longueur);
        init_MinTaille(hauteur,longueur);
        //Main.consumer.accept("init_MinTaille() : " + min_taille);
        //setHauteur_appendice(Math.min(hauteur, longueur));
        //setLongueur_appendice(Math.min(hauteur, longueur));
        gestion_niveau();
        gestion_dimension_bordure();
        fill_liste_cercle();
        fill_list_cercle_controle();
        ajout_decalage();
        ajout_deformation_cadre_locale();
    }
    public Dents(int cote, double x, double y, double hauteur, double longueur, int niveau, boolean dernier, double angle2) {
        super(est_plat);
        //Main.consumer.accept("dans Dents avec niveau");
        posY = y;
        posX = x;
        this.cote = cote;
        this.niveau = niveau;
        this.est_dernier = dernier;
        this.setAngle2(angle2);
        setTailleCotePieceHauteur(hauteur);
        setTailleCotePieceLongueur(longueur);
        init_MinTaille(hauteur,longueur);
        gestion_niveau();
        gestion_dimension_bordure();
        fill_liste_cercle();
        fill_list_cercle_controle();
        ajout_decalage();
        ajout_deformation_cadre_locale();
    }

    public Dents(int cote, double x, double y, double hauteur1, double hauteur2, double longueur1, double longueur2, int niveau, boolean dernier, double angle2) {
        super(est_plat);
        Main.consumer.accept("dans Dents avec hauteur 1 2 et longueur 1 2 et angle 2");
        posY = y;
        posX = x;
        this.cote = cote;
        this.niveau = niveau;
        check_dernier();
        this.setAngle2(angle2);
        init_MinTaille(hauteur1, hauteur2, longueur1, longueur2);
        gestion_niveau();
        gestion_dimension_bordure();
        fill_liste_cercle();
        fill_list_cercle_controle();
        ajout_decalage();
        ajout_deformation_cadre_locale();
    }
    public Dents(int cote, double x, double y, double hauteur, double longueur, int niveau, boolean dernier,double angle1, double angle2) {
        super(est_plat);
        //Main.consumer.accept("dans Dents avec niveau");
        posY = y;
        posX = x;
        this.cote = cote;
        this.niveau = niveau;
        this.est_dernier = dernier;
        this.setAngle1(angle1);
        this.setAngle2(angle2);
        setTailleCotePieceHauteur(hauteur);
        setTailleCotePieceLongueur(longueur);
        init_MinTaille(hauteur,longueur);
        gestion_niveau();
        gestion_dimension_bordure();
        fill_liste_cercle();
        fill_list_cercle_controle();
        ajout_decalage();
        ajout_deformation_cadre_locale();
    }
    public Dents(int cote, double x, double y, double hauteur, double longueur, int niveau) {
        super(est_plat);
        //Main.consumer.accept("dans Dents avec niveau");
        posY = y;
        posX = x;
        this.cote = cote;
        this.niveau = niveau;
        setTailleCotePieceHauteur(hauteur);
        setTailleCotePieceLongueur(longueur);
        init_MinTaille(hauteur,longueur);
        gestion_niveau();
        gestion_dimension_bordure();
        fill_liste_cercle();
        fill_list_cercle_controle();
        ajout_decalage();
        ajout_deformation_cadre_locale();
    }
    // creer un creux a partir des cercle d'une dents de la piece voisine sans aucune operation desssus
    public Dents(ArrayList<Circle> liste1, ArrayList<Circle> liste2) {
        super(est_plat);
        this.liste_cercle = liste1;
        this.liste_cercle_controle = liste2;
    }// pas besoin de faire des transformation pour le placer dans la piece

    public Dents(double x, double y) {
        super(est_plat);
        posX = x;
        posY = y;
        fill_liste_cercle();
        fill_list_cercle_controle();
        ajout_decalage();
        ajout_deformation_cadre_locale();
    }

    Dents() { // Aléatoire complet
        super(est_plat);
        rand = new Random();
        fill_liste_cercle();
        fill_list_cercle_controle();
        ajout_decalage();
        for (int i = 0; i < this.liste_cubicCurveTo.size(); i++) {

            this.liste_cubicCurveTo.get(i).xProperty().bind(this.liste_cercle.get(i).layoutXProperty());
            this.liste_cubicCurveTo.get(i).yProperty().bind(this.liste_cercle.get(i).layoutYProperty());

            this.liste_cubicCurveTo.get(i).controlX1Property().bind(this.liste_cercle_controle.get(2 * i + 1).layoutXProperty());
            this.liste_cubicCurveTo.get(i).controlY1Property().bind(this.liste_cercle_controle.get(2 * i + 1).layoutYProperty());

            this.liste_cubicCurveTo.get(i).controlX2Property().bind(this.liste_cercle_controle.get(2 * i).layoutXProperty());
            this.liste_cubicCurveTo.get(i).controlY2Property().bind(this.liste_cercle_controle.get(2 * i).layoutYProperty());

            this.liste_Moveto.get(i).xProperty().bind(this.liste_cercle.get(i + 1).layoutXProperty());
            this.liste_Moveto.get(i).yProperty().bind(this.liste_cercle.get(i + 1).layoutYProperty());

            notre_path.getElements().add(this.liste_Moveto.get(i));
            notre_path.getElements().add(this.liste_cubicCurveTo.get(i));
        }
    }

    private void init_MinTaille(double hauteur, double longueur) {
        this.min_taille =Math.min(hauteur, longueur);
    }

    private void init_MinTaille(double hauteur1, double hauteur2, double longueur1, double longueur2) {
        double min1 = Math.min(hauteur1, longueur1);
        double min2 = Math.min(hauteur2, longueur2);
        this.min_taille = Math.min(min1, min2);
    }
    private void ajout_decalage(){
        if (!est_decalable) {
            return;
        }
        gestion_decalage();
        // on ne decale que les cercles de l'appendice et non le 0 et le 6
        for (int i = 1; i < this.liste_cercle.size() -1 ; i++) {
            this.liste_cercle.get(i).setLayoutX(this.liste_cercle.get(i).getLayoutX() + this.decalage);
        }
        // on ne decale que les cercles de l'appendice et non le 0 et le 6
        for (int i = 0; i < this.liste_cercle_controle.size() ; i++) {
            this.liste_cercle_controle.get(i).setLayoutX(this.liste_cercle_controle.get(i).getLayoutX() + this.decalage);
        }
    }

    public void ajout_deformation_cadre_locale() {
        if (!est_deformable_localement) {
            return;// alors on ne fait rien
        }
        // on s'occupe des valeur des angles de deformations
        if (cote == Piece.DROITE && est_dernier == false) {
            //Main.consumer.accept(" droite cote == "+this.cote);
            gestion_deformation_locale();
        }
        if (cote == Piece.DROITE && est_dernier ) { // pas utile mais par securité
            setAngle1(0.0);
        }
        //on effectue la rotation et affecte le resultat au premier point de controle
        Circle o1 = this.liste_cercle.get(0);
        Circle m1 = this.liste_cercle_controle.get(0);
        Point p = Piece.calcul_rotation(m1, o1, getAngle1());
        this.liste_cercle_controle.get(0).setLayoutX(p.getX());
        this.liste_cercle_controle.get(0).setLayoutY(p.getY());

        //on effectue la rotation et affecte le resultat au dernier point de controle
        Circle o2 = this.liste_cercle.get(6);
        Circle m2 = this.liste_cercle_controle.get(11);
        Point p2 = Piece.calcul_rotation(m2, o2, getAngle2());
        this.liste_cercle_controle.get(11).setLayoutX(p2.getX());
        this.liste_cercle_controle.get(11).setLayoutY(p2.getY());

    }


    private void gestion_niveau() {
        if (this.niveau == 1) {// si niveau == 1
            // alors les conitions de deformations restes à ceux par defauts
            //  pieces basiques
            this.MARGE_HAUTEUR = 0;
            this.MARGE_HAUTEUR_CONTROLE = 0;
        }else if (this.niveau == 2) { // niveau 2 -> deformation de locale de cadre
            // pieces  basiques
            this.MARGE_HAUTEUR = 0;
            this.MARGE_HAUTEUR_CONTROLE = 0;
            est_deformable_localement = true;
        } else if (this.niveau == 3) { // niveau 3 -> deportation de l'axe
            est_decalable = true;
            this.MARGE_HAUTEUR = 0;
            this.MARGE_HAUTEUR_CONTROLE = 0;
        } else if (this.niveau == 4) { // niveau 4 -> 'approximation' de la position des cercles
            this.MARGE_HAUTEUR = 0;
            this.MARGE_HAUTEUR_CONTROLE = 0;
            est_approximable = true;
            gestion_niveau4();
        } else if (this.niveau == 5) {
            this.MARGE_HAUTEUR = 0;
            this.MARGE_HAUTEUR_CONTROLE = 0;
        }
    }

    private void gestion_niveau4() {
        this.tab_coef_longueur_approx = new double[tab_coef_longeur.length];
        this.tab_coef_hauteur_approx = new double[tab_coef_hauteur.length];
        this.tab_coef_hauteur_controle_approx = new double[tab_coef_hauteur_controle.length];
        this.tab_coef_longueur_controle_approx = new double[tab_coef_longeur_controle.length];
        // on recopie le premier et dernier cercle de controle car on ne fait pas de deformation dessus ni sur les cercle 0 et 6
        fill_tab_ceof_approx();
        fill_tab_coef_controle_approx();
    }
    //rempli le tab de longueur approx si on est dans le niveau 4 en fonction du tab de base et d'une approximation
    private void fill_tab_ceof_approx() {
        for (int i = 0; i < tab_coef_hauteur.length; i++) {
            // approximation verticale
            int signe = new Random().nextInt(2);
            double res = rand_approx(); // coef d'approximation
            if (signe == 0) { // alors approx positive
                tab_coef_hauteur_approx[i] = tab_coef_hauteur[i] + res;
            } else { // sinon négative
                tab_coef_hauteur_approx[i] = tab_coef_hauteur[i] - res;
            }
            // approximation horizontale
            signe = new Random().nextInt(2);
            res = rand_approx(); // coef d'approximation
            if (signe == 0) { // alors approx positive
                tab_coef_longueur_approx[i] = tab_coef_longeur[i] + res;
            } else { // sinon négative
                tab_coef_longueur_approx[i] = tab_coef_longeur[i] - res;
            }
            Main.consumer.accept("valeur de base : h:"+tab_coef_hauteur[i] + " ; l:"+tab_coef_longeur[i]);
            Main.consumer.accept("valeur d' approximation : h:"+tab_coef_hauteur_approx[i] + " ; l:"+tab_coef_longueur_approx[i]);
        }
        Main.consumer.accept("");
        Main.consumer.accept("");
    }

    // remplie le tab des coef avec approx pour les cercles de controles en fonction du coef d'approx deja calculé pour les cercles de tab coef approx
    private void fill_tab_coef_controle_approx() {
        // pour l'appendice : 10 cc et 5 c (et non 12 et 7)
        for (int i = 0; i < tab_coef_longeur_controle.length ; i++) {
            if (i == 0) {
                tab_coef_hauteur_controle_approx[0] =tab_coef_hauteur_controle[0] ;
                tab_coef_longueur_controle_approx[0] =tab_coef_longeur_controle[0] ;
            } else if (i == tab_coef_longeur_controle.length -1 ) {
                tab_coef_hauteur_controle_approx[tab_coef_longeur_controle.length -1 ] =tab_coef_hauteur_controle[tab_coef_longeur_controle.length -1 ] ;
                tab_coef_longueur_controle_approx[tab_coef_longeur_controle.length -1 ] =tab_coef_longeur_controle[tab_coef_longeur_controle.length -1 ] ;
            }else{
                // division par deux pcq chaque cercle de l'appendice a deuc cercle dde controles
                double res = tab_coef_hauteur_approx[(i-1)/2] - tab_coef_hauteur[(i-1)/2]; // on recupere le coef d'ap
                // approximation verticale
                tab_coef_hauteur_controle_approx[i] = tab_coef_hauteur_controle[i] + res;
                // approximation horizontale
                res = tab_coef_longueur_approx[(i-1)/2] - tab_coef_longeur[(i-1)/2]; // on recupere le coef d'ap
                tab_coef_longueur_controle_approx[i] = tab_coef_longeur_controle[i] + res;
            }
            Main.consumer.accept("vaeur de base : h:"+tab_coef_hauteur_controle[i] + " ; l:"+tab_coef_longeur_controle[i]);
            Main.consumer.accept("vaeur d' approximation : h:"+tab_coef_hauteur_controle_approx[i] + " ; l:"+tab_coef_longueur_controle_approx[i]);
        }
    }

    private double rand_approx() {
        double res = new Random().nextDouble() * 0.05;
        return res;
    }
    // gere le calcul de l'angle 1  de deformation de cadre locale de la bordure
    // il n'y a que l'angle 1 a gere car on ne calcule que l'angle 1 de la bordure DROITE (angle du bas)
    private void gestion_deformation_locale() {
        int signe = rand_signe();
        this.setAngle1(signe * calcul_deformation_locale());
    }

    private double calcul_deformation_locale() {
        double angle = 0;
        double random = new Random().nextDouble();
        angle  = (Math.random()*((ANGLE_MAX-ANGLE_MIN)+1))+ANGLE_MIN;
        return angle;
    }
    private void gestion_decalage() {
        int signe = rand_signe(); // signe == 1 || signe  == -1
        this.decalage = (signe * (calcul_decalage() ));//- marge_decalage));
    }

    // calcul la distance de decalage d'un appendice
    private double calcul_decalage() {
        int indice_min = get_indice_min_tab_coef_longeur();
        double minimum = this.liste_cercle.get(indice_min).getLayoutX();
        double diff_min = minimum - this.liste_cercle.get(0).getLayoutX();
        int indice_max = get_indice_max_tab_coef_longeur();
        double maximum = this.liste_cercle.get(indice_max).getLayoutX();
        double diff_max = this.liste_cercle.get(6).getLayoutX() - maximum;
        double hauteur_appendice = calcul_hauteur_appendice_contigu();
        double max_distance = Math.min(diff_min, diff_max) - hauteur_appendice;
        double distance = 0;
        double random = new Random().nextDouble();
        distance = random * max_distance;
        return distance;
    }

    // calcul la distance maximale de decalage  d'axe d'un appendice en fonction de l'appendice de  ses cote contigu
    private double calcul_hauteur_appendice_contigu() {
        double res = 0;
        //res = TAILLE_COTE_PIECE_HAUTEUR * (get_max_tab_coef_hauteur() - this.MARGE_HAUTEUR);
        // -> y0 - y3 = posY - (posY - TAILLZ_COTE_PIECE_HAUTEUR * tab_coef_hauteur[3] -this.MARGE_HAUTEUR )
        // == position du cercle 0 en Y - formule de positionnement du cercle 3 en Y
        res = min_taille * (this.tab_coef_hauteur[get_indice_max_tab_coef_hauteur()] - MARGE_DECALAGE);
        return res;
    }

    private int get_indice_max_tab_coef_hauteur() {
        int indice_max = 0; // 1 est le max des coef pcq coef == ratio sur 1
        for (int i = 0; i < tab_coef_hauteur.length; i++) {
            if (tab_coef_hauteur[i] > tab_coef_hauteur[indice_max]) {
                indice_max = i ;
            }
        }
        return indice_max;

    }

    private int get_indice_max_tab_coef_longeur() {
        int indice_max = 0; // 1 est le max des coef pcq coef == ratio sur 1
        for (int i = 0; i < tab_coef_longeur.length; i++) {
            if (tab_coef_longeur[i] > tab_coef_longeur[indice_max]) {
                indice_max = i;
            }
        }
        return indice_max;
    }
    private int get_indice_min_tab_coef_longeur() {
        int indice_min = 0; // 1 est le max des coef pcq coef == ratio sur 1
        for (int i = 0; i < tab_coef_longeur.length; i++) {
            if (tab_coef_longeur[i] < tab_coef_longeur[indice_min]) {
                indice_min = i;
            }
        }
        return indice_min;
    }
    private int rand_signe() {
        int neg = (int)Math.random() * 2;
        int signe = 0;
        if (neg == 1) { // alors decalage negatif
            signe = -1;
        } else if (neg == 0) { // alors decalage positif
            signe = 1;
        }
        return signe;
    }





    // s'occupe de d'échanger a hauteur et a largeur en fonction du cote ou se
    // situe notre bordure sur la piece
    private void gestion_dimension_bordure() {
        if (this.cote == Piece.HAUT || this.cote == Piece.BAS) {
            // pas de changemetn pour largeur_bordure ni hauteur_bordure
        } else if (this.cote == Piece.GAUCHE || this.cote == Piece.DROITE) {
            //notre bordure est a la verticale et on doit s'occuper de l'axe des x pour l'inversion

            double hauteur = Forme_Bordure.getTailleCotePieceHauteur();
            double longueur = Forme_Bordure.getTailleCotePieceLongueur();
            // on passe la hauteur en longueur
            setTailleCotePieceLongueur(hauteur);
            // et inversemement
            setTailleCotePieceHauteur(longueur);
        }
    }
    // gerer les dimension des bordure dans le cas du niveau 5
    private void gestion_dimension_bordure_niveau5() {
        if (this.cote == Piece.HAUT ) {

        } else if (this.cote == Piece.BAS) {

        } else if (this.cote == Piece.GAUCHE || this.cote == Piece.DROITE) {
            //notre bordure est a la verticale et on doit s'occuper de l'axe des x pour l'inversion

            double hauteur = Forme_Bordure.getTailleCotePieceHauteur();
            double longueur = Forme_Bordure.getTailleCotePieceLongueur();
            // on passe la hauteur en longueur
            setTailleCotePieceLongueur(hauteur);
            // et inversemement
            setTailleCotePieceHauteur(longueur);
        }
    }

    //rempli la liiste de cercle en placatnt le premier point en fonction de posX et posY
    private void fill_liste_cercle() {
        /**
         * on va placer le premier point au coordonne posX posY passé dans le constructeur
         */
        this.liste_cercle.get(0).setLayoutX(posX);
        this.liste_cercle.get(0).setLayoutY(posY);
        /**
         * on place le 6eme cercle sur la meme ligne que le premier et decalé de "LA longueur d'une piece" .
         */
        this.liste_cercle.get(6).setLayoutX(this.liste_cercle.get(0).getLayoutX() + TAILLE_COTE_PIECE_LONGUEUR);
        this.liste_cercle.get(6).setLayoutY(this.liste_cercle.get(0).getLayoutY());
        /**
         * Il nous reste a disposer les 5 points restant , à savoir du 1 à 5 .
         */
        for (int i = 1; i < Forme_Bordure.getNbCercleBordure() - 1; i++) { // on ne s'occcupe ni du cercle 0 ni du 6
            // i - 1 pcq la matrice commencent avec les coef du point 1
            if (niveau == 4) {
                this.liste_cercle.get(i).setLayoutX(this.liste_cercle.get(0).getLayoutX() + (TAILLE_COTE_PIECE_LONGUEUR * tab_coef_longueur_approx[i - 1]));
                this.liste_cercle.get(i).setLayoutY(this.liste_cercle.get(0).getLayoutY() - min_taille * (tab_coef_hauteur_approx[i - 1] - this.MARGE_HAUTEUR));
            }else{
                this.liste_cercle.get(i).setLayoutX(this.liste_cercle.get(0).getLayoutX() + (TAILLE_COTE_PIECE_LONGUEUR * tab_coef_longeur[i - 1]));
                this.liste_cercle.get(i).setLayoutY(this.liste_cercle.get(0).getLayoutY() - min_taille * (tab_coef_hauteur[i - 1] - this.MARGE_HAUTEUR));
            }
        }
        //coloration des cercles pour nos tests de visualisations
        ajout_couleur_cercle();
    }

    // coloris les cercles de lacourbe ---> utile pour nos tests
    private void ajout_couleur_cercle() {
        this.liste_cercle.get(0).setFill(Color.RED);
        this.liste_cercle.get(1).setFill(Color.RED);
        this.liste_cercle.get(2).setFill(Color.RED);
        this.liste_cercle.get(3).setFill(Color.GREEN);
        this.liste_cercle.get(4).setFill(Color.GREEN);
        this.liste_cercle.get(5).setFill(Color.BLUE);
        this.liste_cercle.get(6).setFill(Color.BLUE);
    }

    //rempli la liste de cercle controle en fonction des cercle de la liste de cercle
    private void fill_list_cercle_controle() {
        for (int i = 0; i < this.liste_cercle_controle.size(); i++) { // car les points de controle sont cree 2 à 2
            if (niveau == 4) {
                this.liste_cercle_controle.get(i).setLayoutX(this.liste_cercle.get(0).getLayoutX() + (TAILLE_COTE_PIECE_LONGUEUR * tab_coef_longueur_controle_approx[i]));
            } else {
                this.liste_cercle_controle.get(i).setLayoutX(this.liste_cercle.get(0).getLayoutX() + (TAILLE_COTE_PIECE_LONGUEUR * tab_coef_longeur_controle[i]));
            }
            gestion_x_controle(i);
            if (i == 0 || i == this.liste_cercle_controle.size() - 1) { //premier et dernier point de controle
                this.liste_cercle_controle.get(i).setLayoutY(this.liste_cercle.get(0).getLayoutY() - min_taille * (tab_coef_hauteur_controle[i] - this.MARGE_HAUTEUR_CONTROLE));
            } else {
                if (niveau == 4) {
                    this.liste_cercle_controle.get(i).setLayoutY(this.liste_cercle.get(0).getLayoutY() - min_taille * (tab_coef_hauteur_controle_approx[i] - this.MARGE_HAUTEUR));
                } else {
                    this.liste_cercle_controle.get(i).setLayoutY(this.liste_cercle.get(0).getLayoutY() - min_taille * (tab_coef_hauteur_controle[i] - this.MARGE_HAUTEUR));
                }
            }
        }
    }

    // verifie que la position en x d'un cercle de controle soit valide meme avec le decalage
    private void gestion_x_controle(int indice) {
        if (this.liste_cercle_controle.get(indice).getLayoutX() < this.liste_cercle.get(0).getLayoutX()) {
            //si xn < x0 alors xn == x0
            this.liste_cercle_controle.get(indice).setLayoutX(this.liste_cercle.get(0).getLayoutX());
        }
        else if (this.liste_cercle_controle.get(indice).getLayoutX() > this.liste_cercle.get(6).getLayoutX()) {
            // si xn > x6 alors xn == x6
            this.liste_cercle_controle.get(indice).setLayoutX(this.liste_cercle.get(6).getLayoutX());
        }

    }
    public void place_point(Circle controle1, Circle point_fixe, Circle controle2) { // permet de placer controle 2 par rapport a point_fixe et de controle 1

        double xC1 = controle1.getLayoutX();
        double xRef = point_fixe.getLayoutX();
        double yC1 = controle1.getLayoutY();
        double yRef = point_fixe.getLayoutY();

        double diffX = (xC1 - xRef);
        double diffY = (yC1 - yRef);

        diffX *= -1;
        diffY *= -1;

        controle2.setLayoutX(xRef + diffX);
        controle2.setLayoutY(yRef + diffY);

        System.out.println("X : " + controle2.getLayoutX());
        System.out.println("Y : " + controle2.getLayoutY());
    }


    public Dents(Dents d) {
        super(est_plat);
        //affichage_coord_liste(d.getListe_cercle());
        copie_Coordonnee(d.liste_cercle, d.liste_cercle_controle);
        //affichage_coord_liste(d.getListe_cercle());

    }

    public Dents(Creux c) {
        super(est_plat);
        inversion_Hauteur(c.liste_cercle, c.liste_cercle_controle);
        //cercle_Vers_Courbe();

    }

    // on inverse la hauteur de chaque point par rapport a
    // la hauteur des points cercle 0 et 6 de liste_cercle
    // == symetrie verticale par rapport a l'axe c0;c6
    private void inversion_Hauteur(ArrayList<Circle> liste_cercle, ArrayList<Circle> liste_controleurs) {
        for (int i = 0; i < liste_cercle.size(); i++) {
            Circle c = this.liste_cercle.get(i);
            //on addittionne 2 fois la difference entre le y de c0 et le y de i
            // pour effectuer une symetrie verticale de ci avec l'axe c0;c6
            c.setLayoutY(liste_cercle.get(i).getLayoutY() + 2 * (liste_cercle.get(0).getLayoutY() - liste_cercle.get(i).getLayoutY()));
            //on affecte la psition en x sans la changer
            c.setLayoutX(liste_cercle.get(i).getLayoutX());
        }
        for (int i = 0; i < liste_cercle_controle.size(); i++) {
            Circle c = this.liste_cercle_controle.get(i);
            //on addittionne 2 fois la difference entre le y de c0 et le y de i
            // pour effectuer une symetrie verticale de ci avec l'axe c0;c6
            c.setLayoutY(liste_controleurs.get(i).getLayoutY() + 2 * (liste_cercle.get(0).getLayoutY() - liste_controleurs.get(i).getLayoutY()));
            //on affecte la psition en x sans la changer
            c.setLayoutX(liste_controleurs.get(i).getLayoutX());
        }
    }


    // affecte aux cercles des listes de cercles de this les memes coordonnees que les
    //cercles des listes passées en parametre
    private void copie_Coordonnee(ArrayList<Circle> liste_cercle, ArrayList<Circle> liste_controleurs) {
        for (int i = 0; i < liste_cercle.size(); i++) {
            this.liste_cercle.get(i).setLayoutY(liste_cercle.get(i).getLayoutY());
            this.liste_cercle.get(i).setLayoutX(liste_cercle.get(i).getLayoutX());
        }
        for (int i = 0; i < liste_cercle_controle.size(); i++) {
            this.liste_cercle_controle.get(i).setLayoutY(liste_controleurs.get(i).getLayoutY());
            this.liste_cercle_controle.get(i).setLayoutX(liste_controleurs.get(i).getLayoutX());
        }
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

}

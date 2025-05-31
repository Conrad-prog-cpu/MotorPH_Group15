/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

/**
 *
 * @author santos.conrad
 */
public class Deductions {

    public double calculateSSS(double basicSalary) {
        double[][] sssMatrix = {
            {0, 3249.99, 135.00}, {3250, 3749.99, 157.50}, {3750, 4249.99, 180.00},
            {4250, 4749.99, 202.50}, {4750, 5249.99, 225.00}, {5250, 5749.99, 247.50},
            {5750, 6249.99, 270.00}, {6250, 6749.99, 292.50}, {6750, 7249.99, 315.00},
            {7250, 7749.99, 337.50}, {7750, 8249.99, 360.00}, {8250, 8749.99, 382.50},
            {8750, 9249.99, 405.00}, {9250, 9749.99, 427.50}, {9750, 10249.99, 450.00},
            {10250, 10749.99, 472.50}, {10750, 11249.99, 495.00}, {11250, 11749.99, 517.50},
            {11750, 12249.99, 540.00}, {12250, 12749.99, 562.50}, {12750, 13249.99, 585.00},
            {13250, 13749.99, 607.50}, {13750, 14249.99, 630.00}, {14250, 14749.99, 652.50},
            {14750, 15249.99, 675.00}, {15250, 15749.99, 697.50}, {15750, 16249.99, 720.00},
            {16250, 16749.99, 742.50}, {16750, 17249.99, 765.00}, {17250, 17749.99, 787.50},
            {17750, 18249.99, 810.00}, {18250, 18749.99, 832.50}, {18750, 19249.99, 855.00},
            {19250, 19749.99, 877.50}, {19750, 20249.99, 900.00}, {20250, 20749.99, 922.50},
            {20750, 21249.99, 945.00}, {21250, 21749.99, 967.50}, {21750, 22249.99, 990.00},
            {22250, 22749.99, 1012.50}, {22750, 23249.99, 1035.00}, {23250, 23749.99, 1057.50},
            {23750, 24249.99, 1080.00}, {24250, 24749.99, 1102.50}, {24750, Double.MAX_VALUE, 1125.00}
        };

        for (double[] row : sssMatrix) {
            if (basicSalary >= row[0] && basicSalary <= row[1]) {
                return row[2];
            }
        }
        return 0.0;
    }

    public double calculatePhilHealth(double basicSalary) {
        double monthlyPremium = basicSalary * 0.03;
        if (monthlyPremium < 300) monthlyPremium = 300;
        if (monthlyPremium > 1800) monthlyPremium = 1800;
        return monthlyPremium / 2; // Employee share only
    }

    public double calculatePagIbig(double basicSalary) {
        double contribution = basicSalary > 1500 ? basicSalary * 0.02 : basicSalary * 0.01;
        return Math.min(contribution, 100);
    }

    public double calculateWithholdingTax(double monthlyTaxableIncome) {
        if (monthlyTaxableIncome <= 20832) {
            return 0.0;
        } else if (monthlyTaxableIncome <= 33332) {
            return (monthlyTaxableIncome - 20833) * 0.20;
        } else if (monthlyTaxableIncome <= 66667) {
            return 2500 + (monthlyTaxableIncome - 33333) * 0.25;
        } else if (monthlyTaxableIncome <= 166667) {
            return 10833 + (monthlyTaxableIncome - 66667) * 0.30;
        } else if (monthlyTaxableIncome <= 666667) {
            return 40833.33 + (monthlyTaxableIncome - 166667) * 0.32;
        } else {
            return 200833.33 + (monthlyTaxableIncome - 666667) * 0.35;
        }
    }

    public double getTotalDeductions(double basicSalary, double grossWeekly) {
        double sss = calculateSSS(basicSalary);
        double philHealth = calculatePhilHealth(basicSalary);
        double pagIbig = calculatePagIbig(basicSalary);

        // Monthly taxable = basicSalary - non-tax benefits
        double monthlyTaxable = basicSalary - (sss + philHealth + pagIbig);
        double withholdingTax = calculateWithholdingTax(monthlyTaxable);

        return sss + philHealth + pagIbig + withholdingTax;
    }

    public double getMonthlyWithholdingTax(double basicSalary) {
        double sss = calculateSSS(basicSalary);
        double philHealth = calculatePhilHealth(basicSalary);
        double pagIbig = calculatePagIbig(basicSalary);
        double taxable = basicSalary - (sss + philHealth + pagIbig);
        return calculateWithholdingTax(taxable);
    }
}

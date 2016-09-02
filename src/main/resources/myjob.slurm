#!/bin/bash

#SBATCH --job-name=SudokuSolver
#SBATCH --nodes=1
#SBATCH --ntasks-per-node=1
#SBATCH --cpus-per-task=16
#SBATCH --mem=16000m
#SBATCH --time=00:30:00
#SBATCH --mail-user=NONE
#SBATCH --mail-type=ALL
#SBATCH --export=ALL
#SBATCH --partition=commons

echo "My job ran on:"
pwd

echo SLURM_NODELIST=$SLURM_NODELIST
echo USER=$USER

if [[ -d /home/$USER && -w /home/$USER ]]
then
    cd /home/$USER/SudokuSolver

    java -version
    mvn --version

    mvn -Dtest=SudokuSolverPerformanceTest clean compile test
fi

from model import EcosystemModel
from agents import *
from mesa.visualization.modules import CanvasGrid, ChartModule
from mesa.visualization.ModularVisualization import ModularServer

from model.factory import get_model_parameters

agent_settings = {
    PredatorAgent: {
        "amount_collector": "predator_amount",
        "age_collector": "mean_predator_age",
        "energy_collector": "mean_predator_energy",
        "color": "red",
        "r": 0.6,
        "layer": 0
    },
    PreyAgent: {
        "amount_collector": "prey_amount",
        "age_collector": "mean_prey_age",
        "energy_collector": "mean_prey_energy",
        "color": "green",
        "r": 0.4,
        "layer": 1
    },
    VegetationAgent: {
        "amount_collector": "vegetation_amount",
        "color": "orange",
        "r": 0.2,
        "layer": 2
    },
}

def agent_portrayal(agent: PassiveBeingAgent):
    return {
        "Shape": "circle",
        "Filled": "true",
        "Layer": agent_settings[agent.__class__]["layer"],
        "Color": agent_settings[agent.__class__]["color"],
        "r": agent_settings[agent.__class__]["r"]
    }

parameters = get_model_parameters()

charts = [
    ChartModule(
        [ {"Label": agent_settings[agent]["amount_collector"], "Color": agent_settings[agent]["color"]} 
            for agent in [ PredatorAgent, PreyAgent ] ],
        data_collector_name='datacollector'
    ),
    ChartModule(
        [ {"Label": agent_settings[agent]["age_collector"], "Color": agent_settings[agent]["color"]} 
            for agent in [ PredatorAgent, PreyAgent ] ],
        data_collector_name='datacollector'
    ),
    ChartModule(
        [ {"Label": agent_settings[agent]["energy_collector"], "Color": agent_settings[agent]["color"]} 
            for agent in [ PredatorAgent, PreyAgent ] ],
        data_collector_name='datacollector'
    ),
    ChartModule(
        [ {"Label": agent_settings[agent]["amount_collector"], "Color": agent_settings[agent]["color"]} 
            for agent in [ VegetationAgent ] ],
        data_collector_name='datacollector'
    )
]

grid = CanvasGrid(agent_portrayal, parameters.grid_width, parameters.grid_height, 500, 500)

server = ModularServer(EcosystemModel,
                        [grid, *charts],
                        "Ecosystem Simulation",
                        {
                           "parameters": parameters, 
                            "initial_predator_amount": 10,
                            "initial_prey_amount": 40, 
                            "initial_vegetation_amount": 200
                        })

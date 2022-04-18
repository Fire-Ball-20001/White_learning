using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.Instances;

namespace Interfaces.interfaces
{
    internal interface IBuilder
    {
        IEngine CreateEngine(Part partEngine);
        IWheel CreateWheel(Part partWheel);
        IFrame CreateFrame(Part partFrame);
        IDoor CreateDoor(Part partDoor);
        IBottom CreateBottom(Part partBottom);

        Part[] GetBaseTypes();
        Part[] GetWheelsTypes();
        Part[] GetBottomsTypes();
        Part[] GetFramesTypes();
        Part[] GetDoorsTypes();
        Part[] GetEnginesTypes();
        IMove CreateCar(Part partCar);
    }
}

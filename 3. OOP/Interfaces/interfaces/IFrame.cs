using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Interfaces.interfaces
{
    internal interface IFrame : BaseInterface
    {
        int GetCountWheels();
        int GetCountDoors();
        IDoor[] GetDoors();
        IWheel[] GetWheels();
        bool getUnlimitedDoorsAndWheels();
    }
}
